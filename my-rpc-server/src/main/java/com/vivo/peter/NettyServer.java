package com.vivo.peter;

import com.vivo.peter.handler.CommonDecoder;
import com.vivo.peter.handler.CommonEncoder;
import com.vivo.peter.provider.DefaultServiceProvider;
import com.vivo.peter.registry.NacosServiceRegistry;
import com.vivo.peter.serializer.CommonSerializer;
import com.vivo.peter.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer extends AbstractServer {
    private final CommonSerializer serializer;


    public NettyServer(String hostname, int port) {
        this(hostname, port, 0);
    }

    public NettyServer(String hostname, int port, int serializerCode) {
        this.hostname = hostname;
        this.port = port;
        this.serializer = CommonSerializer.getByCode(serializerCode);
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new DefaultServiceProvider();
        scanServices();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(hostname, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("?????????????????????????????????: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
