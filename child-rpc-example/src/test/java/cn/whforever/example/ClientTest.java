package cn.whforever.example;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.protocol.netty.client.NettyClient;
import cn.whforever.core.proxy.ClientProxy;
import cn.whforever.core.serialize.Serializer;
import cn.whforever.example.service.HelloService;
import cn.whforever.example.service.impl.HelloServiceImpl;

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
                .setSerializer(Serializer.SerializeEnum.JSON.serializer);
        ClientProxy clientProxy = new ClientProxy(clientConfig,new NettyClient(),HelloServiceImpl.class);
        HelloService helloService = (HelloService) clientProxy.refer();
    }
}
