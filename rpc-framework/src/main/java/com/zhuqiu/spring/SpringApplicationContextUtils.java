package com.zhuqiu.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhuqiu
 * @date 2021/3/16
 */
@Component
public class SpringApplicationContextUtils implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringApplicationContextUtils.class);

    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.info("【Spring】注入spring容器开始，context:{}", context);
        if(applicationContext == null) {
            applicationContext = context;
        }
        log.info("【Spring】注入spring容器结束，SpringApplicationContextUtils.applicationContext：{}", applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }
}
