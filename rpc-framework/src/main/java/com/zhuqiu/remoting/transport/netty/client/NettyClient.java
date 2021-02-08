package com.zhuqiu.remoting.transport.netty.client;

import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.transport.netty.codec.NettyKryoDecoder;
import com.zhuqiu.remoting.transport.netty.codec.NettyKryoEncoder;
import com.zhuqiu.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端。获取与服务端的连接
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
@Slf4j
public final class NettyClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    /**
     * 初始化一些资源
     */
    public NettyClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，如果超过了这个时间还无法建立连接，则连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 写超时时间：如果15秒内没有向服务端发送消息，则发送一次心跳请求
                        pipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        // ByteBuf -> RpcResponse
                        pipeline.addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // RpcRequest -> ByteBuf
                        pipeline.addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 与服务端进行连接，并返回Channel，以此向服务端发送信息
     *
     * @param inetSocketAddress 服务端地址
     * @return  Channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功：[{}]", inetSocketAddress.toString());
            } else {
                log.info("客户端连接失败：[{}]", inetSocketAddress.toString());
            }
            completableFuture.complete(future.channel());
        });
        // 当获取到处理结果后，get可以得到值
        return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
