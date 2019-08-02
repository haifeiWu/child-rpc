package cn.whforever.core.registry.consul;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.register.Registry;

import java.util.List;

/**
 * consul 注册中心.
 *
 * @author wuhaifei 2019-08-02
 */
public class ConsulRegistry extends Registry {

    /**
     * 注册中心配置
     *
     * @param registryConfig 注册中心配置
     */
    public ConsulRegistry(RegistryConfig registryConfig) {
        super(registryConfig);
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void register(ServerConfig config) {

    }

    @Override
    public void unRegister(ServerConfig config) {

    }

    @Override
    public void batchUnRegister(List<ServerConfig> configs) {

    }

    @Override
    public List<String> subscribe(ClientConfig config) {
        return null;
    }

    @Override
    public void unSubscribe(ClientConfig config) {

    }

    @Override
    public void batchUnSubscribe(List<ClientConfig> configs) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() {

    }
}
