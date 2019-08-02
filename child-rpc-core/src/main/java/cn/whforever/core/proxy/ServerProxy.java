package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.register.Registry;
import cn.whforever.core.register.RegistryFactory;
import cn.whforever.core.remote.server.AbstractChildServer;
import cn.whforever.core.rpc.RpcInvokerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuhf
 * @Date 2018/9/1 17:21
 **/
public class ServerProxy {

    private AbstractChildServer childServer;
    private Config config;
    private ServerConfig serverConfig;
    private RegistryConfig registryConfig;

    protected transient volatile boolean exported;

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerProxy.class);


    public ServerProxy(AbstractChildServer childServer, Config config) {
        this.childServer = childServer;
        this.config = config;
        serverConfig = (ServerConfig) this.config;
    }

    public ServerProxy(AbstractChildServer nettyServerAbstract) {
        this.childServer = nettyServerAbstract;
    }

    public void export() {
        try {
            Object serviceBean = Class.forName((String) serverConfig.getRef()).newInstance();
            RpcInvokerHandler.serviceMap.put(serverConfig.getInterfaceId(), serviceBean);
            this.childServer.start(this.getServerConfig());

            if (serverConfig.isRegister()) {
                // 将服务注册到zookeeper
                register();
            }
        } catch (Exception e) {
            // 取消服务注册
            unregister();
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
        if (serverConfig.isRegister()) {
            Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
            registry.init();
            registry.start();
            try {
                registry.register(this.serverConfig);
            } catch (ChildRpcRuntimeException e) {
                throw e;
            } catch (Throwable e) {
                String appName = serverConfig.getInterfaceId();
                LOGGER.info(appName, "Catch exception when register to registry: "
                        + registryConfig.getId(), e);
            }
        }
    }

    /**
     * 反注册服务
     */
    protected void unregister() {
        if (serverConfig.isRegister()) {
            Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
            try {
                registry.unRegister(serverConfig);
            } catch (Exception e) {
                String appName = serverConfig.getInterfaceId();
                LOGGER.info(appName, "Catch exception when unRegister from registry: " +
                        registryConfig.getId()
                        + ", but you can ignore if it's called by JVM shutdown hook", e);
            }
        }
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public ServerProxy setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        return this;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServerProxy setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }
}
