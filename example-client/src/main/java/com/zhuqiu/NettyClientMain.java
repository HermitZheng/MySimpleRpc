package com.zhuqiu;

import com.zhuqiu.annotation.RpcProxy;
import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.proxy.RpcClientProxy;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import com.zhuqiu.remoting.transport.netty.client.NettyClient;
import com.zhuqiu.remoting.transport.netty.client.NettyClientTransport;
import com.zhuqiu.remoting.transport.netty.server.NettyServer;
import com.zhuqiu.time.TimeService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
@ComponentScan("com.zhuqiu")
public class NettyClientMain {

//    @RpcProxy
//    private TimeService timeService;

    public static void main(String[] args) {
        ClientTransport rpcClient = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().build();
        RpcClientProxy proxy = new RpcClientProxy(rpcClient, rpcServiceProperties);

        TimeService timeService = proxy.getProxy(TimeService.class);
        String time = timeService.getTime();
        System.out.println(time);
    }
}
