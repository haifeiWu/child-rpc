package cn.whforever.example;

import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.protocol.netty.server.NettyServerAbstract;
import cn.whforever.core.proxy.ServerProxy;
import cn.whforever.core.serialize.AbstractSerializer;
import cn.whforever.example.service.HelloService;
import cn.whforever.example.service.impl.HelloServiceImpl;

/**
 * @author wuhf
 * @Date 2018/9/1 18:30
 **/
public class ServerTest {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setSerializer(AbstractSerializer.SerializeEnum.HESSIAN.serializer)
                .setPort(5201)
                .setInterfaceId(HelloService.class.getName())
                .setRef(HelloServiceImpl.class.getName());
        ServerProxy serverProxy = new ServerProxy(new NettyServerAbstract(),serverConfig);
        try {
            serverProxy.export();
            while (true){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
