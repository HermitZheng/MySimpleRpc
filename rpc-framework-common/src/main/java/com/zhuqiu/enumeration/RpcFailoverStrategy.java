package com.zhuqiu.enumeration;

/**
 * @author zhuqiu
 * @date 2021/1/23
 */
public enum RpcFailoverStrategy {

    // 重试
    RETRY("retry"),
    // 服务降级
    DEGRADE("degrade"),
    // 切换其他服务器
    SWITCH("switch");

    private final String strategyValue;

    RpcFailoverStrategy(String strategyValue) {
        this.strategyValue = strategyValue;
    }

    public String getStrategyValue() {
        return strategyValue;
    }

    public static RpcFailoverStrategy getByValue(String strategyValue) {
        for (RpcFailoverStrategy strategy : values()) {
            if (strategy.getStrategyValue().equals(strategyValue)) {
                return strategy;
            }
        }
        return null;
    }
}
