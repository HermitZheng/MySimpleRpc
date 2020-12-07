package com.zhuqiu.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 给注解的对象进行代理对象的注入，用于注解成员对象
 *
 * @Author zhengqi
 * @Description
 * @Date 11/10/20 2:30 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Component
public @interface RpcProxy {
}
