package com.zhuqiu.remoting.handler;

import com.zhuqiu.exception.RpcException;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.provider.ServiceProvider;
import com.zhuqiu.provider.impl.ServiceProviderImpl;
import com.zhuqiu.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RpcRequest 请求处理器
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class RpcRequestHandler implements ServerHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 处理请求，获取到具体的服务，并调用方法进行处理
     *
     * @param rpcRequest    RPC请求
     * @return  处理结果 Object
     */
    @Override
    public Object handle(RpcRequest rpcRequest) throws RpcException {
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 根据RPC请求获取服务的方法以及具体的参数列表，并调用方法处理，返回结果
     *
     * @param rpcRequest    RPC请求
     * @param service       服务对象
     * @return      服务处理的结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws RpcException {
        Object result;
        Method method;
        try {
            // 根据方法名和参数列表获取相应的方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            // 调用方法处理
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务 Service: [{}] 成功调用方法 Method: [{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
