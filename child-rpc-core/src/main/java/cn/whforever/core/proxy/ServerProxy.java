package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.remote.server.ChildServer;
import cn.whforever.core.rpc.RpcInvokerHandler;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuhf
 * @Date 2018/9/1 17:21
 **/
public class ServerProxy {

    private ChildServer childServer;
    private Config config;

    public ServerProxy (ChildServer childServer,Config config) {
        this.childServer = childServer;
        this.config = config;
    }

    public void export() {
        try {
            ServerConfig serverConfig = (ServerConfig) this.config;
            RpcInvokerHandler.serviceMap.put((String) serverConfig.getInterfaceId(),serverConfig.getRef());
            this.childServer.start(this.config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
