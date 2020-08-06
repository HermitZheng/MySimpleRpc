package com.zhuqiu.provider;

import com.zhuqiu.entity.RpcServiceProperties;

/**
 * 储存并提供服务对象
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
public interface ServiceProvider {

    /**
     * 添加一个服务对象
     *
     * @param service               服务对象
     * @param serviceClass          服务实现类的 接口
     * @param rpcServiceProperties  服务相关的属性
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     * 获取一个服务对象
     *
     * @param rpcServiceProperties  服务相关属性
     * @return  服务对象 Object
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * 发布一个服务（带有具体版本、实现类信息）
     *
     * @param service               服务对象
     * @param rpcServiceProperties  服务相关属性
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    /**
     * 发布一个服务
     *
     * @param service   服务对象
     */
    void publishService(Object service);
}
