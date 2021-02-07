package com.zhuqiu.registry.zk;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.enumeration.RpcErrorMessage;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.loadbalance.LoadBalance;
import com.zhuqiu.loadbalance.RandomLoadBalance;
import com.zhuqiu.registry.ServiceDiscovery;
import com.zhuqiu.registry.zk.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于 Zookeeper 的服务发现
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(RpcServiceProperties rpcService) throws RpcException {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 从 Zookeeper 中找到提供该服务的地址列表
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, rpcService.toRpcServiceName());
        if (childrenNodes.size() == 0) {
            throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND, rpcService.toRpcServiceName());
        }
        // 按照负载均衡规则从中选出一个服务地址
        String serviceAddress = loadBalance.selectServiceAddress(childrenNodes, rpcService);
        log.info("成功找到指定服务, 地址: [{}]", serviceAddress);
        // 分离地址和端口号
        String[] split = serviceAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
