package cn.whforever.example.zookeeper;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.protocol.netty.client.NettyClientAbstract;
import cn.whforever.core.proxy.ClientProxy;
import cn.whforever.core.rpc.RpcConstants;
import cn.whforever.core.serialize.AbstractSerializer;
import cn.whforever.example.service.HelloService;

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
