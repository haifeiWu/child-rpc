package cn.whforever.core.protocol.netty.client;

import cn.whforever.core.coedc.netty.NettyDecoder;
import cn.whforever.core.coedc.netty.NettyEncoder;
import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.Config;
import cn.whforever.core.remote.client.AbstractChildClient;
import cn.whforever.core.rpc.RpcCallbackFuture;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuhaifei
 */
public class NettyClientAbstract extends AbstractChildClient {

    private Channel channel;

    @Override
    public void init(Config config) {
        super.init(config);
        try {
            initNettyClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        ClientConfig clientConfig = (ClientConfig) this.config;
        RpcCallbackFuture future = new RpcCallbackFuture(request);
        RpcCallbackFuture.futurePool.put(request.getRequestId(), future);
        // rpc invoke
        this.writeMsg(request);
        // future get
        return future.get(clientConfig.getTimeoutMillis());
    }

    private void initNettyClient() throws Exception {
        final ClientConfig clientConfig = (ClientConfig) this.config;
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new NettyEncoder(RpcRequest.class, clientConfig.getSerializer()))
                                .addLast(new NettyDecoder(RpcResponse.class, clientConfig.getSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        this.channel = bootstrap.connect(clientConfig.getHost(), clientConfig.getPort()).sync().channel();
    }

    private void writeMsg(RpcRequest request) throws Exception {
        this.channel.writeAndFlush(request).sync();
    }
}
