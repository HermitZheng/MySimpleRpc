package com.zhuqiu.proxy;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.remoting.dto.RpcMessageChecker;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 当使用代理对象去调用方法时，实际上执行的是这个方法
     *
     * @param proxy     代理对象：通过 getProxy() 得到的对象
     * @param method    要调用的方法
     * @param args      参数列表
     * @return          远程方法执行结果
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
        // 使用 CompletableFuture 来接收调用结果，当处理完毕时，可以通过 get() 获取到结果
        CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) clientTransport.sendRpcRequest(rpcRequest);
        rpcResponse = completableFuture.get();
        // 检查 RpcResponse 的有效性
        RpcMessageChecker.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }
}
