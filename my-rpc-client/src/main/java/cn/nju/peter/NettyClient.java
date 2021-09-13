package cn.nju.peter;

import cn.nju.peter.handler.CommonDecoder;
import cn.nju.peter.handler.CommonEncoder;
import cn.nju.peter.loadBalancer.RoundRobinLoadBalancer;
import cn.nju.peter.registry.NacosServiceRegistry;
import cn.nju.peter.registry.ServiceRegistry;
import cn.nju.peter.request.RpcRequest;
import cn.nju.peter.response.RpcResponse;
import cn.nju.peter.serializer.CommonSerializer;
import cn.nju.peter.serializer.JsonSerializer;
import cn.nju.peter.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient implements Client{
    private static final Bootstrap bootstrap;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ServiceRegistry serviceRegistry = new NacosServiceRegistry(new RoundRobinLoadBalancer());
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            String hostname = inetSocketAddress.getHostName();
            int port = inetSocketAddress.getPort();
            ChannelFuture future = bootstrap.connect(hostname, port).sync();
            log.info("客户端连接到服务器 {}:{}", hostname, port);
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        log.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生: ", e);
        }

        return null;
    }
}
