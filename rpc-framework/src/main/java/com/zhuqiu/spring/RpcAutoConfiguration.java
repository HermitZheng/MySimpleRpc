package com.zhuqiu.spring;

import com.zhuqiu.remoting.transport.netty.client.NettyClient;
import com.zhuqiu.remoting.transport.netty.server.NettyServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuqiu
 * @date 2021/1/20
 */
@Configuration
public class RpcAutoConfiguration {

//    @Bean
//    @ConditionalOnBean
//    public NettyServer nettyServer() {
//        return new NettyServer();
//    }
//
//    @Bean
//    @ConditionalOnBean
//    public NettyClient nettyClient() {
//        return new NettyClient();
//    }
}
