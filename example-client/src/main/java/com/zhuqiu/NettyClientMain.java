package com.zhuqiu;

import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.proxy.RpcClientProxy;
import com.zhuqiu.remoting.transport.netty.ClientTransport;
import com.zhuqiu.remoting.transport.netty.client.NettyClientTransport;
import com.zhuqiu.time.TimeService;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
public class NettyClientMain {

    public static void main(String[] args) {
        ClientTransport rpcClient = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().build();
        RpcClientProxy proxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
        TimeService timeService = proxy.getProxy(TimeService.class);
        String time = timeService.getTime();
        System.out.println(time);
    }
}
