服务端开发都会或多或少的涉及到 RPC 的使用，当然如果止步于会用，对自己的成长很是不利，所以楼主今天本着知其然，且知其所以然的精神来探讨一下 RPC 这个东西。
<!--more-->
## child-rpc模型
child-rpc 采用 socket 直连的方式来实现服务的远程调用，然后使用 jdk 动态代理的方式让调用者感知不到远程调用。
![child-rpc模型]( http://img.whforever.cn/rpc-model.png "")

## child-rpc 开箱使用

### 发布服务
RPC 服务类要监听指定IP端口，设置要发布的服务的实现及其接口的引用，并指定序列化的方式，目前 child-rpc 支持 Hessian，JACKSON 两种序列化方式。

```java
/**
 * @author wuhf
 * @Date 2018/9/1 18:30
 **/
public class ServerTest {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setSerializer(Serializer.SerializeEnum.HESSIAN.serializer)
                .setPort(5201)
                .setInterfaceId(HelloService.class.getName())
                .setRef(HelloServiceImpl.class.getName());
        ServerProxy serverProxy = new ServerProxy(new NettyServer(),serverConfig);
        try {
            serverProxy.export();
            while (true){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 引用服务
RPC 客户端要链接远程 IP 端口，并注册要引用的服务，然后调用 sayHi 方法，输出结果
```java
/**
 * @author wuhf
 * @Date 2018/9/1 18:31
 **/
public class ClientTest {

    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHost("127.0.0.1")
                .setPort(5201)
                .setTimeoutMillis(100000)
                .setSerializer(Serializer.SerializeEnum.HESSIAN.serializer);
        ClientProxy clientProxy = new ClientProxy(clientConfig,new NettyClient(),HelloService.class);
        for (int i = 0; i < 10; i++) {
            HelloService helloService = (HelloService) clientProxy.refer();
            System.out.println(helloService.sayHi());
        }
    }
}
```
### 运行
server 端输出
![rpc-srever]( http://img.whforever.cn/rpc-server.png "")

client 端输出
![rpc-client]( http://img.whforever.cn/rpc-client.png "")

## child-rpc 具体实现 

### RPC 请求，响应消息实体定义
定义消息请求响应格式，消息类型、消息唯一 ID 和消息的 json 序列化字符串内容。消息唯一 ID 是用来客户端验证服务器请求和响应是否匹配。

```java
// rpc 请求
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -4364536436151723421L;

    private String requestId;
    private long createMillisTime;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    // set get 方法省略掉
}
// rpc 响应
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 7329530374415722876L;

    private String requestId;
    private Throwable error;
    private Object result;
    // set get 方法省略掉
}
```

### 网络传输过程中的编码解码

消息编码解码使用自定义的编解码器，根据服务初始化是使用的序列化器来将数据序列化成字节流，拆包的策略是设定指定长度的数据包，对 socket 粘包，拆包感兴趣的小伙伴请移步 [Socket 中粘包问题浅析及其解决方案](http://www.hchstudio.cn/article/2018/d5b3/ ) 

下面是解码器代码实现 ：
```java
public class NettyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private Serializer serializer;

    public NettyDecoder(Class<?> genericClass, Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();
        // 读取消息长度
        int dataLength = byteBuf.readInt();
        
        if (dataLength < 0) {
            channelHandlerContext.close();
        }

        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        try {
            byte[] data = new byte[dataLength];
            byteBuf.readBytes(data);
            Object object = serializer.deserialize(data,genericClass);
            list.add(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

下面是编码器的实现：

```java
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private Serializer serializer;

    public NettyEncoder(Class<?> genericClass,Serializer serializer) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(object)) {
            byte[] data = serializer.serialize(object);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
```
### RPC 业务逻辑处理 handler

server 端业务处理 handler 实现 : 主要业务逻辑是 通过 java 的反射实现方法的调用。

```java
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // invoke 通过调用反射方法获取 rpcResponse
        RpcResponse response = RpcInvokerHandler.invokeService(rpcRequest);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>> child-rpc provider netty server caught exception", cause);
        ctx.close();
    }
}

public class RpcInvokerHandler {
    public static Map<String, Object> serviceMap = new HashMap<String, Object>();
    public static RpcResponse invokeService(RpcRequest request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object serviceBean = serviceMap.get(request.getClassName());

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t);
        }
        return response;
    }
}
```

client 端主要业务实现是等待 server 响应返回。代码比较简单就不贴代码了，详情请看下面给出的 github 链接。
### RPC 服务端与客户端启动
因为服务端与客户端启动都是 Netty 的模板代码，因为篇幅原因就不贴出来了，感兴趣的伙伴请移步 [造个轮子---RPC动手实现]( https://github.com/haifeiWu/child-rpc )。

## 小结 
因为只是为了理解 RPC 的本质，所以在实现细节上还有好多没有仔细去雕琢的地方。不过 RPC 的目的就是允许像调用本地服务一样调用远程服务，对调用者透明，于是我们使用了动态代理。并使用 Netty 的 handler 发送数据和响应数据，总的来说该框架实现了简单的 RPC 调用。代码比较简单，主要是思路，以及了解 RPC 底层的实现。

## 参考文章

- [造个轮子---RPC动手实现]( https://github.com/haifeiWu/child-rpc )
- [Socket 中粘包问题浅析及其解决方案](https://www.hchstudio.cn/article/2018/d5b3/ )


![关注我们](https://img.hchstudio.cn/CodePig-QRCode.jpg "关注我们")
