package com.zhuqiu;

import com.zhuqiu.annotation.EnableRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
@ComponentScan("com.zhuqiu")
@EnableRpcServer
public class NettyServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
//        NettyServer nettyServer = applicationContext.getBean(NettyServer.class);
//        TimeService timeService = new TimeServiceImpl();
//        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().build();
        // Spring注解：RpcService 已经将服务进行注册
//        nettyServer.registerService(timeService, rpcServiceProperties);
//        nettyServer.start();
    }
}
