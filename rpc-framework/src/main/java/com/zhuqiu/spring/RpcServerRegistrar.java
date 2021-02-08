package com.zhuqiu.spring;

import com.zhuqiu.remoting.transport.netty.server.NettyServer;
import org.springframework.context.annotation.Bean;

/**
 * 为 服务提供者 注入 NettyServer 服务端，并启动
 *
 * @author zhuqiu
 * @date 2021/2/8
 */
public class RpcServerRegistrar {

    @Bean
    public NettyServer nettyServer() {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
        return nettyServer;
    }
}
