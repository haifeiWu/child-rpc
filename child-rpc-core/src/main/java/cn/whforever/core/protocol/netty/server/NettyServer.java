package cn.whforever.core.protocol.netty.server;

import cn.whforever.core.coedc.netty.NettyDecoder;
import cn.whforever.core.coedc.netty.NettyEncoder;
import cn.whforever.core.config.Config;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.remote.server.ChildServer;
import cn.whforever.core.rpc.RpcRequest;
import cn.whforever.core.rpc.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer extends ChildServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private Thread thread;

    @Override
    public void start(Config config) throws Exception {
        final ServerConfig serverConfig = (ServerConfig) config;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    channel.pipeline()
                                            .addLast(new NettyDecoder(RpcRequest.class, serverConfig.getSerializer()))
                                            .addLast(new NettyEncoder(RpcResponse.class, serverConfig.getSerializer()))
                                            .addLast(new NettyServerHandler());
                                }
                            })
                            .option(ChannelOption.SO_TIMEOUT, 100)
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_REUSEADDR, true)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);
                    ChannelFuture future = bootstrap.bind(serverConfig.getPort()).sync();
                    logger.info(">>>>>>>>>>> child-rpc server start success, appName={}, port={}", NettyServer.class.getName(), serverConfig.getPort());
                    Channel serviceChannel = future.channel().closeFuture().sync().channel();
                } catch (InterruptedException e) {
                    logger.error("", e);
                } finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            }
        });
//        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
        logger.info(">>>>>> netty server interrupt");
    }
}
