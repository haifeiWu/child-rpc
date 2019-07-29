package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.register.Registry;
import cn.whforever.core.register.RegistryFactory;
import cn.whforever.core.remote.server.AbstractChildServer;
import cn.whforever.core.rpc.RpcInvokerHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuhf
 * @Date 2018/9/1 17:21
 **/
public class ServerProxy {

    private AbstractChildServer childServer;
    private Config config;

    protected transient volatile boolean exported;

    public ServerProxy(AbstractChildServer childServer, Config config) {
        this.childServer = childServer;
        this.config = config;
    }

    public void export() {
        try {
            ServerConfig serverConfig = (ServerConfig) this.config;
            Object serviceBean = Class.forName((String) serverConfig.getRef()).newInstance();
            RpcInvokerHandler.serviceMap.put((String) serverConfig.getInterfaceId(), serviceBean);
            this.childServer.start(this.config);

            // 将服务注册到zookeeper
            register();
        } catch (Exception e) {
            if (e instanceof ChildRpcRuntimeException) {
                throw (ChildRpcRuntimeException) e;
            } else {
                throw new ChildRpcRuntimeException("Build provider proxy error!", e);
            }
        }
        exported = true;
    }

    /**
     * 注册服务
     */
    protected void register() {
        if (true) {
            List<RegistryConfig> registryConfigs = new ArrayList<>();//providerConfig.getRegistry();
            if (registryConfigs != null) {
                for (RegistryConfig registryConfig : registryConfigs) {
                    Registry registry = RegistryFactory.getRegistry(registryConfig);
//                    registry.init();
                    registry.start();
                    try {
//                        registry.register(providerConfig);
                    } catch (ChildRpcRuntimeException e) {
                        throw e;
                    } catch (Throwable e) {
//                        String appName = providerConfig.getAppName();
//                        if (LOGGER.isWarnEnabled(appName)) {
//                            LOGGER.warnWithApp(appName, "Catch exception when register to registry: "
//                                    + registryConfig.getId(), e);
//                        }
                    }
                }
            }
        }
    }
}
