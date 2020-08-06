package com.zhuqiu.registry.zk;

import com.zhuqiu.registry.ServiceRegistry;
import com.zhuqiu.registry.zk.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 基于 Zookeeper 的服务注册
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // 拼接得到 Zookeeper 的 ZNode 节点路径
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 在 Zookeeper 中创建一个持久化的节点，即注册服务
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
