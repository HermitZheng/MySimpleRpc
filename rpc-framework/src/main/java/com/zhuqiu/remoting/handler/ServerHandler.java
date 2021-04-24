package com.zhuqiu.remoting.handler;

import com.zhuqiu.exception.RpcException;
import com.zhuqiu.remoting.dto.RpcRequest;

/**
 * @author zhuqiu
 * @date 2021/4/24
 */
public interface ServerHandler {

    /**
     * 根据 请求体 中的信息进行方法调用的处理
     *
     * @param rpcRequest    请求体
     * @return  处理结果
     * @throws RpcException
     */
    Object handle(RpcRequest rpcRequest) throws RpcException;
}
