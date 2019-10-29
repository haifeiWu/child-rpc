package cn.whforever.core.config;

import cn.whforever.core.serialize.Serializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:05
 **/
public class ServerConfig extends Config {

    /**
     * 接口实现类引用
     */
    protected Object ref;
    private int port;
    private String host;
    private Serializer serializer;
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

    public Serializer getSerializer() {
        return serializer;
    }

    public ServerConfig setSerializer(Serializer serializer) {
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
