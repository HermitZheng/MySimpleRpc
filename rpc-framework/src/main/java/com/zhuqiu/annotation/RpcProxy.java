package com.zhuqiu.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
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
