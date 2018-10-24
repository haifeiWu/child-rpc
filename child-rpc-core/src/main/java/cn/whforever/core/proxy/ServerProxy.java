package cn.whforever.core.proxy;

import cn.whforever.core.config.Config;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.remote.server.AbstractChildServer;
import cn.whforever.core.rpc.RpcInvokerHandler;

/**
 * @author wuhf
 * @Date 2018/9/1 17:21
 **/
public class ServerProxy {

    private AbstractChildServer childServer;
    private Config config;

    public ServerProxy (AbstractChildServer childServer, Config config) {
        this.childServer = childServer;
        this.config = config;
    }

    public void export() {
        try {
            ServerConfig serverConfig = (ServerConfig) this.config;
            Object serviceBean = Class.forName((String) serverConfig.getRef()).newInstance();
            RpcInvokerHandler.serviceMap.put((String) serverConfig.getInterfaceId(),serviceBean);
            this.childServer.start(this.config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
