package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.remote.client.AbstractChildClient;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

import java.util.UUID;

/**
 * @author wuhf
 * @Date 2018/9/1 17:04
 **/
public class ClientProxy implements Proxy {
    private AbstractChildClient childClient;
    private Config config;
    private Class<?> iface;

    public ClientProxy(Config config, AbstractChildClient childClient, Class<?> iface) {
        this.childClient = childClient;
        this.config = config;
        this.iface = iface;
    }

    public Object refer() {
        try {
            childClient.init(this.config);
            return invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object invoke() {
        return java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread()
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
                        throw new Exception(">>>>>>>>>>> child-rpc netty response not found.");
                    }

                    if (null != response.getError()) {
                        throw response.getError();
                    }
                    
                    return response.getResult();
                });
    }

}
