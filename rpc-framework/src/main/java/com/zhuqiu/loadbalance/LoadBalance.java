package com.zhuqiu.loadbalance;

import com.zhuqiu.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡接口
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
public interface LoadBalance {

    /**
     * 从已有的服务地址列表中，选择其中一个地址并返回
     *
     * @param serviceAddresses  服务地址列表
     * @return      一个服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);

    /**
     * 根据不同的请求信息，从已有的服务列表中，选择其中一个地址返回
     *
     * @param serviceAddresses  服务地址列表
     * @param request           请求体
     * @return      一个服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest request);

}
