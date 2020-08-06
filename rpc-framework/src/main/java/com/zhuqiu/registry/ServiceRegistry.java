package com.zhuqiu.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName    注册的服务名
     * @param inetSocketAddress 服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
