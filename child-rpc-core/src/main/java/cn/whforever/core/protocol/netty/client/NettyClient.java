package cn.whforever.core.protocol.netty.client;

import cn.whforever.core.remote.client.ChildClient;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;

public class NettyClient extends ChildClient {
    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        return null;
    }
}
