package com.zhuqiu.remoting.transport.netty;

import com.zhuqiu.remoting.dto.RpcRequest;

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
    Object sendRpcRequest(RpcRequest rpcRequest);
}
