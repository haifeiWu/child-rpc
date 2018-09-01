package cn.whforever.core.config;

import cn.whforever.core.serialize.Serializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:05
 **/
public class ServerConfig implements Config {

    private int port;
    private Serializer serializer;

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public ServerConfig setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }
}
