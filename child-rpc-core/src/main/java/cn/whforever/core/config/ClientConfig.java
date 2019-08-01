package cn.whforever.core.config;

import cn.whforever.core.serialize.AbstractSerializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:02
 **/
public class ClientConfig extends Config {

    private String host;
    private int port;
    protected long timeoutMillis;
    private AbstractSerializer serializer;

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

    public AbstractSerializer getSerializer() {
        return serializer;
    }

    public ClientConfig setSerializer(AbstractSerializer serializer) {
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
