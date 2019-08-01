package cn.whforever.core.config;

/**
 * @author wuhaifei
 */
public abstract class Config {
    /**
     * 是否注册，如果是false只订阅不注册
     */
    protected boolean                                register         = true;

    /**
     * 是否订阅服务
     */
    protected boolean                                subscribe        = true;

    /**
     * 服务接口：做为服务唯一标识的组成部分<br>
     */
    protected String                                 interfaceId;

    public String getInterfaceId() {
        return interfaceId;
    }

    public Config setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    public boolean isRegister() {
        return register;
    }

    public Config setRegister(boolean register) {
        this.register = register;
        return this;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public Config setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
        return this;
    }
}
