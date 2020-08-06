package com.zhuqiu.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhuqiu
 * @date 2020/8/2
 */
@AllArgsConstructor
@Getter
@ToString
public enum  RpcResponseCode {

    SUCCESS(200, "远程调用方法成功"),
    FAIL(500, "远程调用方法失败");

    private final int code;

    private final String message;
}
