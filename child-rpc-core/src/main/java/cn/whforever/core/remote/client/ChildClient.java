package cn.whforever.core.remote.client;

import cn.whforever.core.config.Config;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;
import cn.whforever.core.serialize.Serializer;

public abstract class ChildClient {
    // ---------------------- config ----------------------
    protected Config config;

    public void init(Config config) {
        this.config = config;
    }

    // ---------------------- operate ----------------------

    public abstract RpcResponse send(RpcRequest request) throws Exception;
}
