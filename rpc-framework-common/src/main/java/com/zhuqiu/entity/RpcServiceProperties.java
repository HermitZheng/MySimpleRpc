package com.zhuqiu.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author zhuqiu
 * @date 2020/8/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties implements Serializable {

    /**
     * 服务的版本号
     */
    private String version;

    /**
     * 如果指定接口有多个实现，则使用group来区分
     */
    private String group;

    /**
     * 服务的接口名称
     */
    private String serviceName;

    /**
     * 获取具体的服务实现类
     *
     * @return  服务实现类名称
     */
    public String toRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
