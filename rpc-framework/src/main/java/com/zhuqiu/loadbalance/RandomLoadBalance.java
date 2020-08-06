package com.zhuqiu.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 使用 随机算法 的负载均衡实现类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}