package com.zhuqiu.exception;

import com.zhuqiu.enumeration.RpcErrorMessage;

/**
 * @author zhuqiu
 * @date 2020/8/3
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessage rpcErrorMessage, String detail) {
        super(rpcErrorMessage.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessage rpcErrorMessage) {
        super(rpcErrorMessage.getMessage());
    }
}
