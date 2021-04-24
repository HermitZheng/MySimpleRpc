package com.zhuqiu.remoting.executors;

import com.zhuqiu.enumeration.RpcResponseCode;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.handler.ServerHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuqiu
 * @date 2021/4/24
 */
@Slf4j
public class RpcRequestRunnable implements Runnable{

    private final ServerHandler serverHandler;
    private final RpcRequest rpcRequest;
    private final ChannelHandlerContext ctx;

    public RpcRequestRunnable(ServerHandler serverHandler, RpcRequest rpcRequest, ChannelHandlerContext ctx) {
        this.serverHandler = serverHandler;
        this.rpcRequest = rpcRequest;
        this.ctx = ctx;
    }

    @Override
    public void run() throws RpcException {
        try {
            Object result = serverHandler.handle(rpcRequest);
            log.info(String.format("服务端获取到处理请求: %s", result.toString()));
            checkAndSend(ctx, result, rpcRequest);
        } catch (RpcException e) {
            // 调用失败，fallback
            RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
            ctx.writeAndFlush(fail).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            log.error("服务调用失败，返回错误响应，msg: [{}]", e.getMessage());
        }
    }

    private void checkAndSend(ChannelHandlerContext ctx, Object result, RpcRequest rpcRequest) {
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            // 如果连接正常，且通道可以写入，则返回结果响应
            RpcResponse<Object> success = RpcResponse.success(result, rpcRequest.getRequestId());
            ctx.writeAndFlush(success).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            // 无法进行写入，则丢弃
            RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
            ctx.writeAndFlush(fail).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            log.error("当前无法传输消息，丢弃消息");
        }
    }
}
