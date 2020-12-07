package com.zhuqiu.loadbalance;

import com.zhuqiu.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 基于请求信息的负载均衡抽象类
 * 会根据不同的请求进行不同的服务地址分配
 *
 * @author zhengqi
 * @date 2020/8/4
 */
public abstract class AbstractRequestLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return new RandomLoadBalance().selectServiceAddress(serviceAddresses);
    }

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest request) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, request);
    }

    /**
     * 根据请求信息来进行不同的选择
     *
     * @param serviceAddresses  服务地址列表
     * @param request           请求体
     * @return      一个服务地址
     */
    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest request);
}
