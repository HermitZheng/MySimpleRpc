package com.zhuqiu.remoting.transport.netty.server;

import com.zhuqiu.enumeration.RpcMessageType;
import com.zhuqiu.enumeration.RpcResponseCode;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.executors.ServerExecutor;
import com.zhuqiu.remoting.handler.RpcRequestHandler;
import com.zhuqiu.remoting.executors.RpcRequestRunnable;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

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
            ExecutorService executor = SingletonFactory.getInstance(ServerExecutor.class).getExecutor();
            executor.execute(new RpcRequestRunnable(rpcRequestHandler, rpcRequest, ctx));
        } catch (RpcException e) {
            // 调用失败，fallback
            RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
            ctx.writeAndFlush(fail).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            log.error("服务调用失败，返回错误响应，msg: [{}]", e.getMessage());
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
