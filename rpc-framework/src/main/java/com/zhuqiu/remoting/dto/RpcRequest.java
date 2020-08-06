package com.zhuqiu.remoting.dto;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.enumeration.RpcMessageType;
import lombok.*;

import java.io.Serializable;

/**
 * 请求消息体（数据传输对象）
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -9115274355149741360L;
    private String requestId;
    /**
     * 请求的接口名
     */
    private String interfaceName;

    /**
     * 请求方法名
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] parameters;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 消息类型
     */
    private RpcMessageType rpcMessageType;

    /**
     * 服务的版本
     */
    private String version;

    /**
     * 接口的多个实现用group来区分
     */
    private String group;

    /**
     * 构造得到一个服务信息类，即具体的服务实现类信息
     *
     * @return  RpcServiceProperties
     */
    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder()
                .serviceName(this.getInterfaceName())
                .group(this.getGroup())
                .version(this.getVersion())
                .build();
    }
}
