## child-rpc
netty实现的轻量级rpc框架

## 发布服务

```java
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

## 引用服务

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