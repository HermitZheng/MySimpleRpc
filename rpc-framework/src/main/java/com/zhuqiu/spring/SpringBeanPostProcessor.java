package com.zhuqiu.spring;

import com.zhuqiu.annotation.RpcProxy;
import com.zhuqiu.annotation.RpcService;
import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.provider.ServiceProvider;
import com.zhuqiu.provider.impl.ServiceProviderImpl;
import com.zhuqiu.proxy.RpcClientProxy;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import com.zhuqiu.remoting.transport.netty.client.NettyClientTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 在 Bean 创建完成之前（属性赋值之后）调用这个类的方法，来检查目标类是否被注解
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    private final RpcClientProxy proxy;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

        ClientTransport rpcClient = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().build();
        proxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            registerBean(bean);
        }
        return bean;
    }

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 对于每个 @RpcProxy 注解的对象，为其依赖注入代理
            if (field.isAnnotationPresent(RpcProxy.class)) {
                log.info("Field [{}] of Class [{}] is annotated with [{}]", field.getName(), bean.getClass().getName(), RpcProxy.class.getCanonicalName());
                field.setAccessible(true);
                try {
                    field.set(bean, proxyBean(field.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    private Object proxyBean(Class<?> bean) {
        return proxy.getProxy(bean);
    }

    private void registerBean(Object bean) {
        log.info("Class [{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
        // 获取 @RpcService 注解对象
        RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
        // 根据注解信息，构建 RpcServiceProperties
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .version(annotation.version())
                .group(annotation.group())
                .build();
        // 发布服务
        serviceProvider.publishService(bean, rpcServiceProperties);
    }
}
