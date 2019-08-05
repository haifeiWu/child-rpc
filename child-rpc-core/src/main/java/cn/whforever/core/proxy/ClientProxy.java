package cn.whforever.core.proxy;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.Config;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.register.Registry;
import cn.whforever.core.register.RegistryFactory;
import cn.whforever.core.remote.client.AbstractChildClient;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author wuhf
 * @Date 2018/9/1 17:04
 **/
public class ClientProxy<T> implements Proxy {
    private AbstractChildClient childClient;
    private Config config;
    private ClientConfig clientConfig;
    private Class<T> iface;
    private RegistryConfig registryConfig;

    public ClientProxy(Config config, AbstractChildClient childClient, Class<T> iface) {
        this.childClient = childClient;
        this.config = config;
        this.config.setInterfaceId(iface.getName());
        this.iface = iface;
    }

    public T refer() {
        try {
            if (config.isSubscribe()) {
                subscribe();
            }
            childClient.init(this.clientConfig);
            return invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unRef() {
    }

    /**
     * 订阅zk的服务列表.
     */
    private void subscribe() {
        Registry registry = RegistryFactory.getRegistry(this.getRegistryConfig());
        registry.init();
        registry.start();

        this.clientConfig = (ClientConfig) config;
        List<String> providerList = registry.subscribe(this.clientConfig);

        if (null == providerList) {
            throw new ChildRpcRuntimeException("无可用服务供订阅！");
        }

        // 使用随机算法，随机选择一个provider
        int index = ThreadLocalRandom.current().nextInt(providerList.size());
        String providerInfo = providerList.get(index);
        String[] providerArr = providerInfo.split(":");
        clientConfig = (ClientConfig) this.config;
        clientConfig.setHost(providerArr[0]);
        clientConfig.setPort(Integer.parseInt(providerArr[1]));
    }

//    private void unSubscribe() {
//
//    }

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
