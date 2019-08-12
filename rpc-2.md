> 原文地址： [haifeiWu和他朋友们的博客](https://www.hchstudio.cn/article/2019/ba29/?_ref=github) <br/>博客地址：[www.hchstudio.cn](https://www.hchstudio.cn/article/2019/ba29/?_ref=github) <br/>欢迎转载，转载请注明作者及出处，谢谢！

前段时间自己搞了个 RPC 的轮子，不过相对来说比较简单，最近在原来的基础上加以改造，使用 Zookeeper 实现了 provider 自动寻址以及消费者的简单负载均衡，对之前的感兴趣的请转 [造个轮子---RPC动手实现]( https://www.hchstudio.cn/article/2018/b674/ )。

## RPC 模型
在原来使用 TCP 直连的基础上实现基于 Zookeeper 的服务的注册与发现，改造后的依赖关系是这样的。

![child-rpc](https://img.hchstudio.cn/child-rpc2.png)

## 怎么用
话不多说，我们来看下如何发布和引用服务。
服务端我们将服务的 IP 和端口号基础信息注册到 Zookeeper 上。
```java
/**
 * @author wuhaifei 2019-08-02
 */
public class ZookeeperServerMainTest {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setSerializer(AbstractSerializer.SerializeEnum.HESSIAN.serializer)
                .setHost("172.16.30.114")
                .setPort(5201)
                .setRef(HelloServiceImpl.class.getName())
                .setRegister(true)
                .setInterfaceId(HelloService.class.getName());

        RegistryConfig registryConfig = new RegistryConfig().setAddress("127.0.0.1:2181")
                .setSubscribe(true)
                .setRegister(true)
                .setProtocol(RpcConstants.ZOOKEEPER);
        ServerProxy serverProxy = new ServerProxy(new NettyServerAbstract())
                .setServerConfig(serverConfig)
                .setRegistryConfig(registryConfig);
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

通过 Zookeeper 引用注册在其上的服务。
```java
/**
 * @author wuhaifei 2019-08-02
 */
public class ZookeeperClientMainTest {
    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProtocol(RpcConstants.ZOOKEEPER)
                .setTimeoutMillis(100000)
                .setSerializer(AbstractSerializer.SerializeEnum.HESSIAN.serializer);

        RegistryConfig registryConfig = new RegistryConfig()
                .setAddress("127.0.0.1:2181")
                .setProtocol(RpcConstants.ZOOKEEPER)
                .setRegister(true)
                .setSubscribe(true);
        ClientProxy<HelloService> clientProxy = new ClientProxy(clientConfig, new NettyClientAbstract(), HelloService.class)
                .setRegistryConfig(registryConfig);
        for (int i = 0; i < 10; i++) {
            HelloService helloService = clientProxy.refer();
            System.out.println(helloService.sayHi());
        }
    }
}
```

运行结果就不一一贴出了，感兴趣的小伙伴可以查看楼主传到 github 上的源码[这是一个rpc的轮子](https://github.com/haifeiWu/child-rpc.git)。

## 服务的发布与订阅
楼主在原来代码的基础上添加了 Zookeeper 的注册的逻辑，原来的代码相关介绍请转 [造个轮子---RPC动手实现]( https://www.hchstudio.cn/article/2018/b674/ )。

### 服务的发布
```java
/**
* 发布服务
*/
public void export() {
    try {
        Object serviceBean = Class.forName((String) serverConfig.getRef()).newInstance();
        RpcInvokerHandler.serviceMap.put(serverConfig.getInterfaceId(), serviceBean);
        this.childServer.start(this.getServerConfig());

        if (serverConfig.isRegister()) {
            // 将服务注册到zookeeper
            register();
        }
    } catch (Exception e) {
        // 取消服务注册
        unregister();
        if (e instanceof ChildRpcRuntimeException) {
            throw (ChildRpcRuntimeException) e;
        } else {
            throw new ChildRpcRuntimeException("Build provider proxy error!", e);
        }
    }
    exported = true;
}

/**
 * 注册服务
 */
protected void register() {
    if (serverConfig.isRegister()) {
        Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
        registry.init();
        registry.start();
        try {
            registry.register(this.serverConfig);
        } catch (ChildRpcRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            String appName = serverConfig.getInterfaceId();
            LOGGER.info(appName, "Catch exception when register to registry: "
                    + registryConfig.getId(), e);
        }
    }
}

```
### 服务的订阅

```java
/**
* 服务的引用.
*/
public T refer() {
    try {
        if (config.isSubscribe()) {
            subscribe();
        }
        childClient.init(this.clientConfig);
        return invoke();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

/**
 * 订阅zk的服务列表.
 */
private void subscribe() {
    Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
    registry.init();
    registry.start();

    this.clientConfig = (ClientConfig) config;
    List<String> providerList = registry.subscribe(this.clientConfig);

    if (null == providerList) {
        throw new ChildRpcRuntimeException("无可用服务供订阅！");
    }

    // 使用随机算法，随机选择一个provider
    int index = ThreadLocalRandom.current().nextInt(providerList.size());
    String providerInfo = providerList.get(index);
    String[] providerArr = providerInfo.split(":");
    clientConfig = (ClientConfig) this.config;
    clientConfig.setHost(providerArr[0]);
    clientConfig.setPort(Integer.parseInt(providerArr[1]));
}
```

上面代码比较简单，就是在原来直连的基础上添加 zk 的操作，在发布服务的时候将 provider 的 IP 和端口号基础信息注册到 zk 上，在引用服务的时候使用随机算法从 zk 上选取可用的 provider 信息，然后进行 invoke 调用。


## 小结
RPC（Remote procedure call）底层逻辑相对来说比较简单，楼主在实现的过程中参考了其他 RPC 框架的部分代码，受益匪浅~