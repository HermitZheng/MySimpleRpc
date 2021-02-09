package com.zhuqiu.provider.impl;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.enumeration.RpcErrorMessage;
import com.zhuqiu.exception.RpcException;
import com.zhuqiu.provider.ServiceProvider;
import com.zhuqiu.registry.ServiceRegistry;
import com.zhuqiu.registry.zk.ZkServiceRegistry;
import com.zhuqiu.remoting.transport.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuqiu
 * @date 2020/8/3
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    /**
     * key：     远程服务名称（interface name + version + group）
     * value：   服务对象 Object
     */
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = new ZkServiceRegistry();
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {
        // interface name + version + group
        String serviceName = rpcServiceProperties.toRpcServiceName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        // 将服务写入缓存记录中
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("Add service: {} and interfaces: {}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if (service == null) {
            throw new RpcException(RpcErrorMessage.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, RpcServiceProperties.builder().group("").version("").build());
    }

    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            // 获取服务的接口名
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            // 返回标准形式的全限定类名，如：com.zhuqiu.service.HelloService
            String serviceName = serviceRelatedInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);
            this.addService(service, serviceRelatedInterface, rpcServiceProperties);
            // 注册服务（使用Zookeeper）
            int port = NettyServer.serverPort();
            serviceRegistry.registerService(rpcServiceProperties.toRpcServiceName(), new InetSocketAddress(host, port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
