package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.register.Registry;
import cn.whforever.core.register.RegistryFactory;
import cn.whforever.core.remote.client.AbstractChildClient;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

import java.util.UUID;

/**
 * @author wuhf
 * @Date 2018/9/1 17:04
 **/
public class ClientProxy<T> implements Proxy {
    private AbstractChildClient childClient;
    private Config config;
    private Class<T> iface;
    private RegistryConfig registryConfig;

    public ClientProxy(Config config, AbstractChildClient childClient, Class<T> iface) {
        this.childClient = childClient;
        this.config = config;
        this.iface = iface;
    }

    public T refer() {
        try {
            if (config.isSubscribe()) {
                subscribe();
            }
            childClient.init(this.config);
            return invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 订阅zk的服务列表.
     */
    private void subscribe() {
        Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
        registry.init();
        registry.start();

        // 使用随机算法，随机选择一个provider
    }

    public T invoke() {
        return (T) java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread()
                        .getContextClassLoader(), new Class[]{iface},
                (proxy, method, args) -> {

                    // request
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setCreateMillisTime(System.currentTimeMillis());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

                    // send
                    RpcResponse response = childClient.send(request);

                    // valid response
                    if (response == null) {
                        throw new ChildRpcRuntimeException(">>>>>>>>>>> child-rpc netty response not found.");
                    }

                    if (null != response.getError()) {
                        throw new ChildRpcRuntimeException(response.getError());
                    }

                    return response.getResult();
                });
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public ClientProxy setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        return this;
    }
}
