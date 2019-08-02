package cn.whforever.example.zookeeper;

import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.protocol.netty.server.NettyServerAbstract;
import cn.whforever.core.proxy.ServerProxy;
import cn.whforever.core.rpc.RpcConstants;
import cn.whforever.core.serialize.AbstractSerializer;
import cn.whforever.example.service.HelloService;
import cn.whforever.example.service.impl.HelloServiceImpl;

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
