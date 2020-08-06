package com.zhuqiu.spring;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.zhuqiu.annotation.RpcService;
import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.provider.ServiceProvider;
import com.zhuqiu.provider.impl.ServiceProviderImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 在 Bean 创建之前调用这个类的方法，来检查目标类是否被注解
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
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
        return bean;
    }
}
