package com.zhuqiu.loadbalance;

import com.zhuqiu.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 使用 一致性Hash算法 的负载均衡实现类
 *
 * @author zhengqi
 * @date 2020/8/4
 */
public class ConsistentHashingLoadBalance extends AbstractRequestLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest request) {
        return null;
    }
}
