package com.zhuqiu.proxy;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.enumeration.RpcConfigProperties;
import com.zhuqiu.enumeration.RpcFailoverStrategy;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.remoting.dto.RpcMessageChecker;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import com.zhuqiu.utils.file.PropertiesFileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 动态代理类
 * 当一个被代理的类调用一个方法时，实际上是调用了这个动态代理类提供的方法
 * 通过代理调用远程的方法，实际上就像调用本地方法一样（对于调用者来说，内部实现被隐藏起来了）
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final ClientTransport clientTransport;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(ClientTransport clientTransport, RpcServiceProperties rpcServiceProperties) {
        this.clientTransport = clientTransport;
        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
    }

    /**
     * 获取代理对象
     */
    public <T> T getProxy(Class<T> clazz) {
        /**
         * params: ClassLoader, interfaces[], InvocationHandler
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 当使用代理对象去调用方法时，实际上执行的是这个方法
     *
     * @param proxy  代理对象：通过 getProxy() 得到的对象
     * @param method 要调用的方法
     * @param args   参数列表
     * @return 远程方法执行结果
     * @throws Throwable
     */
    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法: [{}]", method.getName());
        // 构建请求，包含接口名称、方法名称、参数列表等各种信息
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        try {
            // 使用 CompletableFuture 来接收调用结果，当处理完毕时，可以通过 get() 获取到结果
            CompletableFuture<RpcResponse<Object>> completableFuture =
                    clientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get(15, TimeUnit.SECONDS);
            // 检查 RpcResponse 的有效性
            RpcMessageChecker.check(rpcResponse, rpcRequest);
        } catch (RpcException e) {
            log.error("调用方法: [{}] 失败", method.getName());
            // 调用故障转移方案
            rpcResponse = failover(rpcRequest);
        }
        return rpcResponse.getData();
    }

    private RpcResponse<Object> failover(RpcRequest rpcRequest) {
        // 获取配置文件中设置的故障转移策略
        Properties properties = PropertiesFileUtils.readProperties(RpcConfigProperties.RPC_CONFIG_PATH.getPropertyValue());
        String strategy = RpcFailoverStrategy.RETRY.getStrategyValue();
        if (properties != null) {
            strategy = properties.getProperty(RpcConfigProperties.FAILOVER_STRATEGY.getPropertyValue(), strategy);
        }
        RpcResponse<Object> rpcResponse;
        // 获取降级服务的实现类
        Class<?> degradation = rpcServiceProperties.getDegradation();

        switch (RpcFailoverStrategy.getByValue(strategy)) {
            // 直接调用降级服务
            case DEGRADE:
                rpcResponse = clientTransport.serviceDegradation(rpcRequest, degradation);
                break;
            // 只进行重试 retry
            case SWITCH:
                rpcResponse = retry(rpcRequest);
                break;
            // 重试，最后调用降级服务
            default:
                // TODO 配置文件设置重试次数，以及是否降级
                rpcResponse = retry(rpcRequest, true, degradation);
                break;
        }
        return rpcResponse;
    }

    private RpcResponse<Object> retry(RpcRequest rpcRequest, boolean isDegrade, Class<?> degradation) {
        int retryTimes = 3;
        RpcResponse<Object> rpcResponse = null;
        boolean flag = false;
        for (int i = 0; i < retryTimes; i++) {
            try {
                CompletableFuture<RpcResponse<Object>> completableFuture = clientTransport.sendRpcRequest(rpcRequest);
                rpcResponse = completableFuture.get(15, TimeUnit.SECONDS);
                if (RpcMessageChecker.check(rpcResponse, rpcRequest)) {
                    flag = true;
                    break;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | RpcException e) {
                log.error("调用方法: [{}] 失败, 重试次数: [{}]", rpcRequest.getMethodName(), i+1);
            }
        }
        if (!flag && isDegrade && degradation != null) {
            rpcResponse = clientTransport.serviceDegradation(rpcRequest, degradation);
        }
        return rpcResponse;
    }

    private RpcResponse<Object> retry(RpcRequest rpcRequest) {
        return retry(rpcRequest, false, null);
    }
}
