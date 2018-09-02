package cn.whforever.core.config;

import cn.whforever.core.serialize.Serializer;

/**
 * @author wuhf
 * @Date 2018/9/1 15:05
 **/
public class ServerConfig implements Config {

    private int port;
    private Serializer serializer;
    /**
     * 接口实现类引用
     */
    protected Object ref;

    private Object interfaceId;

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

    public Object getInterfaceId() {
        return interfaceId;
    }

    public ServerConfig setInterfaceId(Object interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }
}
