package cn.whforever.core.config;

import cn.whforever.core.serialize.Serializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:02
 **/
public class ClientConfig extends Config {

    protected long timeoutMillis;
    private String host;
    private int port;
    private Serializer serializer;

    /**
     * 协议
     */
    private String protocol;

    public String getHost() {
        return host;
    }

    public ClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ClientConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public ClientConfig setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public ClientConfig setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public ClientConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }
}
