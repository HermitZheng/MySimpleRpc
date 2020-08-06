package com.zhuqiu.remoting.dto;

import com.zhuqiu.enumeration.RpcResponseCode;
import lombok.*;

import java.io.Serializable;

/**
 * 响应消息体（数据传输对象）
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 3654755381482773397L;
    private String requestId;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 远程调用成功，返回Response
     *
     * @param data      调用返回的数据
     * @param requestId 请求Id
     * @param <T>       响应数据的类型
     * @return          Response
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCode.SUCCESS.getCode());
        response.setMessage(RpcResponseCode.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    /**
     * 远程调用失败，返回错误信息
     *
     * @param rpcResponseCode   错误码枚举
     * @param <T>               响应数据类型
     * @return                  Response
     */
    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCode.getCode());
        response.setMessage(rpcResponseCode.getMessage());
        return response;
    }
}
