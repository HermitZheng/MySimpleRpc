package com.zhuqiu.remoting.transport.netty.client;

import com.zhuqiu.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放并管理一些服务器还未处理的请求
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    /**
     * 登记一个还未处理的请求
     *
     * @param requestId 请求Id
     * @param future    CompletableFuture<RpcResponse>，未来可以从其中得到响应结果
     */
    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * 使一个未处理的请求完成，即赋予其处理结果；将其从未处理 Map 中移除，之后可以使用 future.get() 来获取结果
     *
     * @param rpcResponse
     */
    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (future != null) {
            // 在complete中会用CAS替换result，然后当我们get时，如果可以获取到值的时候就可以返回了。
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
