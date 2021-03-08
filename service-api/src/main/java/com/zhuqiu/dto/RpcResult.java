package com.zhuqiu.dto;

/**
 * @author zhuqiu
 * @date 2021/3/4
 */
public class RpcResult<E> {

    private E result;

    private String serverName;

    public RpcResult(E result, String serverName) {
        this.result = result;
        this.serverName = serverName;
    }

    public E getResult() {
        return result;
    }

    public String getServerName() {
        return serverName;
    }

    public void setResult(E result) {
        this.result = result;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "result=" + result +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
