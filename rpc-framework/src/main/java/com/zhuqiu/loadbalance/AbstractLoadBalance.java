package com.zhuqiu.loadbalance;

import com.zhuqiu.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡抽象类（无视请求信息）
 * 不会对于不同的请求信息作出不同的选择
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses);
    }

    /**
     * 默认调用只基于服务地址列表的负载均衡算法
     */
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest request) {
        return selectServiceAddress(serviceAddresses);
    }

    /**
     * 从列表中选择一个地址并返回
     *
     * @param serviceAddresses  服务地址列表
     * @return      一个服务地址
     */
    protected abstract String doSelect(List<String> serviceAddresses);
}
