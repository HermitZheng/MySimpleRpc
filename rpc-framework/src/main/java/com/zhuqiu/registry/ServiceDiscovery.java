package com.zhuqiu.registry;

import com.zhuqiu.entity.RpcServiceProperties;

import java.net.InetSocketAddress;

/**
 * 服务发现
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名来寻找服务
     *
     * @param rpcServiceName    服务名称
     * @return      服务地址
     */
    InetSocketAddress lookupService(RpcServiceProperties rpcServiceName);
}
