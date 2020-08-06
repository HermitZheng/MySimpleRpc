package com.zhuqiu.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RPC服务注解，用于注解实现类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface RpcService {

    /**
     * 服务的版本号
     */
    String version() default "";

    /**
     * 服务的group，用于区分多个实现类
     * @return
     */
    String group() default "";
}
