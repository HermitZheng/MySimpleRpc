package com.zhuqiu.annotation;

import com.zhuqiu.spring.RpcServerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 在 服务提供者 的Spring主启动类上注解，以注册 NettyServer 服务端并启动
 *
 * @author zhuqiu
 * @date 2021/2/8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcServerRegistrar.class)
public @interface EnableRpcServer {
}
