package cn.whforever.core.remote.client;

import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;
import cn.whforever.core.serialize.Serializer;

public abstract class ChildClient {
    // ---------------------- config ----------------------
    protected String serverAddress;
    protected Serializer serializer;
    protected long timeoutMillis;

    public void init(String serverAddress, Serializer serializer, long timeoutMillis) {
        this.serverAddress = serverAddress;
        this.serializer = serializer;
        this.timeoutMillis = timeoutMillis;
    }

    // ---------------------- operate ----------------------

    public abstract RpcResponse send(RpcRequest request) throws Exception;
}
