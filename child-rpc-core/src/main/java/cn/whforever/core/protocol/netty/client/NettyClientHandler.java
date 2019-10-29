package cn.whforever.core.protocol.netty.client;

import cn.whforever.core.rpc.RpcCallbackFuture;
import cn.whforever.core.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端业务处理类。
 *
 * @author wuhf
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(rpcResponse.getRequestId());
        future.setResponse(rpcResponse);
        RpcCallbackFuture.futurePool.put(rpcResponse.getRequestId(), future);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(">>>>>>>>>>> child-rpc netty client caught exception", cause);
        ctx.close();
    }
}
