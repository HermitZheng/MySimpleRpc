package com.zhuqiu.dto;

import java.io.Serializable;

/**
 * @author zhuqiu
 * @date 2021/3/4
 */
public class RpcResult<E> implements Serializable {

    private static final long serialVersionUID = 8426371058090573323L;

    private E result;

    private String serverName;

    private String serviceName;

    private int code;

    private String message;

    private enum Status {
        // 成功
        SUCCESS(200, "success"),
        // 失败
        FAIL(400, "fail");

        int code;
        String status;

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        Status(int code, String status) {
            this.code = code;
            this.status = status;
        }
    }

    public static<E> RpcResult<E> fail(E result, String serverName) {
        return new RpcResult<>(result, serverName, Status.FAIL);
    }

    public static<E> RpcResult<E> success(E result, String serverName) {
        return new RpcResult<>(result, serverName, Status.SUCCESS);
    }

    public static<E> RpcResult<E> fail(E result, String serverName, String serviceName) {
        return new RpcResult<>(result, serverName, Status.FAIL, serviceName);
    }

    public static<E> RpcResult<E> success(E result, String serverName, String serviceName) {
        return new RpcResult<>(result, serverName, Status.SUCCESS, serviceName);
    }

    public RpcResult(E result, String serverName, Status status) {
        this.result = result;
        this.serverName = serverName;
        this.code = status.code;
        this.message = status.status;
    }

    public RpcResult(E result, String serverName, Status status, String serviceName) {
        this.result = result;
        this.serverName = serverName;
        this.serviceName = serviceName;
        this.code = status.code;
        this.message = status.status;
    }

    public RpcResult(E result, String serverName) {
        this.result = result;
        this.serverName = serverName;
        this.code = Status.SUCCESS.code;
        this.message = Status.SUCCESS.status;
    }

    public RpcResult(Status status) {
        this.code = status.code;
        this.message = status.status;
    }

    public RpcResult() {
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "result=" + result +
                ", serverName='" + serverName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
