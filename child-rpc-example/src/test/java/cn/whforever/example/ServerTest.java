package cn.whforever.example;

import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.protocol.netty.server.NettyServer;
import cn.whforever.core.proxy.ServerProxy;
import cn.whforever.core.serialize.Serializer;

/**
 * @author wuhf
 * @Date 2018/9/1 18:30
 **/
public class ServerTest {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setSerializer(Serializer.SerializeEnum.JSON.serializer).setPort(5201);
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
