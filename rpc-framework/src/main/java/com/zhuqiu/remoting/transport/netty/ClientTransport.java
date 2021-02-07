package com.zhuqiu.remoting.transport.netty;

import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 传输RpcRequest
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
public interface ClientTransport {

    /**
     * 发送请求至服务端
     *
     * @param rpcRequest    RPC请求
     * @return      服务端返回的数据对象
     */
    CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest);

    /**
     * 服务降级：如果本地有实现的方法，则执行本地方法
     *
     * @param rpcRequest    RPC请求
     * @return      本地方法返回的数据对象
     */
    default RpcResponse<Object> serviceDegradation(RpcRequest rpcRequest, Class<?> degradation) {
        return RpcResponse.degrade(null, rpcRequest.getRequestId());
    }
}
