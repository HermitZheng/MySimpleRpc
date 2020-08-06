package com.zhuqiu.remoting.transport.netty.client;

import com.zhuqiu.enumeration.RpcMessageType;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 自定义客户端 Handler 来处理服务端发送的消息
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final ChannelProvider channelProvider;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    /**
     * 读取服务器发送的消息
     *
     * @param ctx   上下文
     * @param msg   接受到的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("客户端接收到消息：[{}]", msg);
            if (msg instanceof RpcResponse) {
                RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
                // 赋予响应结果
                unprocessedRequests.complete(rpcResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 一段时间没有发生 写事件
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                // 根据地址获取channel
                Channel channel = channelProvider.get((InetSocketAddress) ctx.channel().remoteAddress());
                // 构建一次心跳请求
                RpcRequest rpcRequest = RpcRequest.builder().rpcMessageType(RpcMessageType.HEART_BEAT).build();
                // 发送心跳并进行监听，如果请求失败则关闭channel连接
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 当客户端处理消息发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生异常：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
