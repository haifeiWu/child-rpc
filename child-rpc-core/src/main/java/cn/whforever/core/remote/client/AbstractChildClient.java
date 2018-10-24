package cn.whforever.core.remote.client;

import cn.whforever.core.config.Config;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

/**
 * @author wuhf
 * @data 2018-10-24
 */
public abstract class AbstractChildClient {
    // ---------------------- config ----------------------
    protected Config config;

    public void init(Config config) {
        this.config = config;
    }

    // ---------------------- operate ----------------------

    public abstract RpcResponse send(RpcRequest request) throws Exception;
}
