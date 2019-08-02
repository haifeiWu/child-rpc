package cn.whforever.core.register;

import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.registry.consul.ConsulRegistry;
import cn.whforever.core.registry.zk.ZookeeperRegistry;
import cn.whforever.core.rpc.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory of Registry.
 *
 * @author wuhaifei
 */
public class RegistryFactory {

    /**
     * 保存全部的配置和注册中心实例
     */
    private final static ConcurrentMap<RegistryConfig, Registry> ALL_REGISTRIES = new ConcurrentHashMap<RegistryConfig, Registry>();

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryFactory.class);

    /**
     * 得到注册中心对象
     *
     * @param registryConfig RegistryConfig类
     * @return Registry实现
     */
    public static synchronized Registry getRegistry(RegistryConfig registryConfig) {
        if (ALL_REGISTRIES.size() > 3) { // 超过3次 是不是配错了？
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Size of registry is greater than 3, Please check it!");
            }
        }
        try {
            // 注意：RegistryConfig重写了equals方法，如果多个RegistryConfig属性一样，则认为是一个对象
            Registry registry = ALL_REGISTRIES.get(registryConfig);
            if (registry == null) {
                // TODO 使用SPI的方式生成服务注册
//                ExtensionClass<Registry> ext =ext ExtensionLoaderFactory.getExtensionLoader(Registry.class)
//                        .getExtensionClass(registryConfig.getProtocol());
//                if (ext == null) {
//                    throw ExceptionUtils.buildRuntime("registry.protocol", registryConfig.getProtocol(),
//                            "Unsupported protocol of registry config !");
//                }
//                registry = ext.getExtInstance(new Class[]{RegistryConfig.class}, new Object[]{registryConfig});

//                ServiceLoader<Registry> services = ServiceLoader.load(Registry.class);
//                Iterator iterator = services.iterator();
//                while (iterator.hasNext()) {
//                    registry = (Registry) iterator.next();
//                }

                if (RpcConstants.ZOOKEEPER.equalsIgnoreCase(registryConfig.getProtocol())) {
                    registry = new ZookeeperRegistry(registryConfig);
                } else if (RpcConstants.CONSUL.equalsIgnoreCase(registryConfig.getProtocol())) {
                    registry = new ConsulRegistry(registryConfig);
                }
                ALL_REGISTRIES.put(registryConfig, registry);
            }
            return registry;
        } catch (ChildRpcRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new ChildRpcRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 得到全部注册中心配置
     *
     * @return 注册中心配置
     */
    public static List<RegistryConfig> getRegistryConfigs() {
        return new ArrayList<RegistryConfig>(ALL_REGISTRIES.keySet());
    }

    /**
     * 得到全部注册中心
     *
     * @return 注册中心
     */
    public static List<Registry> getRegistries() {
        return new ArrayList<Registry>(ALL_REGISTRIES.values());
    }

    /**
     * 关闭全部注册中心
     */
    public static void destroyAll() {
        for (Map.Entry<RegistryConfig, Registry> entry : ALL_REGISTRIES.entrySet()) {
            RegistryConfig config = entry.getKey();
            Registry registry = entry.getValue();
            try {
//                registry.destroy();
                ALL_REGISTRIES.remove(config);
            } catch (Exception e) {
                LOGGER.error("Error when destroy registry :" + config
                        + ", but you can ignore if it's called by JVM shutdown hook", e);
            }
        }
    }
}
