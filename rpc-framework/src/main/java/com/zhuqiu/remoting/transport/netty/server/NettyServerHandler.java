package com.zhuqiu.remoting.transport.netty.server;

import com.zhuqiu.enumeration.RpcMessageType;
import com.zhuqiu.enumeration.RpcResponseCode;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义服务端 Handler 来处理客户端发送的消息
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("服务端接收到消息：", msg);
            RpcRequest rpcRequest = (RpcRequest) msg;
            // 如果是心跳请求
            if (rpcRequest.getRpcMessageType() == RpcMessageType.HEART_BEAT) {
                log.info("接收到客户端发送的心跳请求");
                return;
            }
            // 调用方法处理器处理并返回结果
            Object result = rpcRequestHandler.handle(rpcRequest);
            log.info(String.format("服务端获取到处理请求: %s", result.toString()));
            // 如果连接正常，且通道可以写入，则返回结果响应
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                RpcResponse<Object> success = RpcResponse.success(result, rpcRequest.getRequestId());
                ctx.writeAndFlush(success).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                // 无法进行写入，则丢弃
            } else {
                RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
                ctx.writeAndFlush(fail).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                log.error("当前无法传输消息，丢弃消息");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 一段时间没有读取到消息
            if (state == IdleState.READER_IDLE) {
                log.info("服务端未读取到信息，发生空闲事件，关闭连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生异常：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
