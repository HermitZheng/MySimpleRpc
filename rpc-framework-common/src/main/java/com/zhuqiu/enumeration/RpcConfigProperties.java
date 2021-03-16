package com.zhuqiu.enumeration;

/**
 * @author zhuqiu
 * @date 2020/8/4
 */
public enum RpcConfigProperties {

    // 默认的配置文件名
    RPC_CONFIG_PATH("rpc.properties"),
    // 配置文件中ZK的地址
    ZK_ADDRESS("rpc.zookeeper.address"),
    // 配置文件中故障转移策略
    FAILOVER_STRATEGY("rpc.failover.strategy"),
    // 服务端配置文件设置的的端口
    SERVER_PORT("rpc.server.port"),
    // 服务端配置文件设置的的Host
    SERVER_HOST("rpc.server.host");

    private final String propertyValue;

    RpcConfigProperties(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}

