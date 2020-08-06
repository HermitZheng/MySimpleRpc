package com.zhuqiu.registry.zk.utils;

import com.zhuqiu.enumeration.RpcConfigProperties;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.utils.file.PropertiesFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Curator （ Zookeeper 客户端框架 ）工具类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class CuratorUtils {

    /**
     * 重试连接间隔时间
     */
    private static final int BASE_SLEEP_TIME = 1000;
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 3;
    /**
     * Zookeeper 根节点路径
     */
    public static final String ZK_REGISTER_ROOT_PATH = "/my_rpc";
    /**
     * 服务节点路径缓存 Map
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    /**
     * 已注册的服务的路径 Set
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    private static CuratorFramework zkClient;
    private static String defaultZookeeperAddress = "127.0.0.1:2181";

    private CuratorUtils() {
    }

    /**
     * 创建一个持久化节点
     *
     * @param zkClient    Curator 客户端对象
     * @param servicePath 节点路径的组成部分：服务名称
     */
    public static void createPersistentNode(CuratorFramework zkClient, String servicePath) {
        try {
            if (REGISTERED_PATH_SET.contains(servicePath) || zkClient.checkExists().forPath(servicePath) != null) {
                log.info("节点: [{}] 已经存在！", servicePath);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                log.info("节点: [{}] 创建成功！", servicePath);
            }
            REGISTERED_PATH_SET.add(servicePath);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 获取一个节点下的所有子节点
     *
     * @param zkClient       Curator 客户端对象
     * @param rpcServiceName RPC服务名称 如：com.zhuqiu.HelloService
     * @return
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(servicePath, result);
            registerWatcher(zkClient, servicePath);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
        return result;
    }

    /**
     * 删除所有已注册的服务
     *
     * @param zkClient  Curator 客户端对象
     */
    public static void clearRegister(CuratorFramework zkClient) {
        REGISTERED_PATH_SET.stream().parallel().forEach(path -> {
            try {
                zkClient.delete().forPath(path);
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e.getCause());
            }
        });
        log.info("已删除所有注册的服务: [{}]", REGISTERED_PATH_SET.toString());
    }

    /**
     * 获取 Curator 客户端对象
     *
     * @return  CuratorFramework
     */
    public static CuratorFramework getZkClient() {
        // 设置 Zookeeper 服务地址
        Properties properties = PropertiesFileUtils.readPropertiesFile(RpcConfigProperties.RPC_CONFIG_PATH.getPropertyValue());
        if (properties != null) {
            defaultZookeeperAddress = properties.getProperty(RpcConfigProperties.ZK_ADDRESS.getPropertyValue());
        }
        // 如果已有正在连接的 Zookeeper 客户端，则直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // 设置重试规则，创建客户端连接
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(defaultZookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }

    /**
     * 对指定节点进行监听，即监听服务的变化并进行更新
     *
     * @param zkClient          Curator 客户端对象
     * @param rpcServiceName    RPC服务名称
     */
    private static void registerWatcher(CuratorFramework zkClient, String rpcServiceName) {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        // 对指定路径下的节点进行监听
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        // 如果发生了变化，则进行更新
        PathChildrenCacheListener pathChildrenCacheListener = (client, event) -> {
            List<String> serviceAddress = client.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }
}
