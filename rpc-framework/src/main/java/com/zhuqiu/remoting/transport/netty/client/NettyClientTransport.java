package com.zhuqiu.remoting.transport.netty.client;

import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.registry.ServiceDiscovery;
import com.zhuqiu.registry.zk.ZkServiceDiscovery;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * 基于 Netty 向服务端发送RPC请求
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyClientTransport() {
        serviceDiscovery = new ZkServiceDiscovery();
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 构建 CompletableFuture 来接收返回值
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
        // 服务发现，获取服务地址
        InetSocketAddress serviceAddress = serviceDiscovery.lookupService(rpcRequest.toRpcProperties().toRpcServiceName());
        // 根据服务地址获取对应的 Channel 通道
        Channel channel = channelProvider.get(serviceAddress);
        // 如果连接正常
        if (channel != null && channel.isActive()) {
            // 将请求登记到未处理请求集合中
            unprocessedRequests.put(rpcRequest.getRequestId(), completableFuture);
            // 向服务端发送请求
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送消息: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("发送请求失败: ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return completableFuture;
    }
}
