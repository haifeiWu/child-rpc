package cn.whforever.core.protocol.netty.server;

import cn.whforever.core.rpc.RpcInvokerHandler;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * server端业务逻辑处理.
 *
 * @author wuhf
 * @data 2018/07/09/03
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // invoke
        RpcResponse response = RpcInvokerHandler.invokeService(rpcRequest);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>> child-rpc provider netty server caught exception", cause);
        ctx.close();
    }
}
