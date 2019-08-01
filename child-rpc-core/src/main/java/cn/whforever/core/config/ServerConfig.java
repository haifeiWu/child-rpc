package cn.whforever.core.config;

import cn.whforever.core.serialize.AbstractSerializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:05
 **/
public class ServerConfig extends Config {

    private int port;
    private String host;
    private AbstractSerializer serializer;
    /**
     * 接口实现类引用
     */
    protected Object ref;

    /**
     * 协议
     */
    private String protocol = "";

    private boolean register = true;

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public AbstractSerializer getSerializer() {
        return serializer;
    }

    public ServerConfig setSerializer(AbstractSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * Gets ref.
     *
     * @return the ref
     */
    public Object getRef() {
        return ref;
    }

    /**
     * Sets ref.
     *
     * @param ref the ref
     * @return the ref
     */
    public ServerConfig setRef(Object ref) {
        this.ref = ref;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public ServerConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }
}
