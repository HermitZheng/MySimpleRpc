package com.zhuqiu.remoting.transport.netty.server;

import com.zhuqiu.config.CustomShutdownHook;
import com.zhuqiu.entity.RpcServiceProperties;
import com.zhuqiu.enumeration.RpcConfigProperties;
import com.zhuqiu.factory.SingletonFactory;
import com.zhuqiu.provider.ServiceProvider;
import com.zhuqiu.provider.impl.ServiceProviderImpl;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.remoting.transport.netty.codec.NettyKryoDecoder;
import com.zhuqiu.remoting.transport.netty.codec.NettyKryoEncoder;
import com.zhuqiu.serialize.kryo.KryoSerializer;
import com.zhuqiu.utils.file.PropertiesFileUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 服务端。接收客户端的请求，根据客户端发送的信息调用相应的方法处理
 * 并将调用结果返回给客户端
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
@Component
@Slf4j
public class NettyServer implements InitializingBean {

    private final KryoSerializer kryoSerializer = new KryoSerializer();
    private static int DEFAULT_PORT = 9998;
    public static int PORT = 0;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public NettyServer() {
        PORT = serverPort();
    }

    public static int serverPort() {
        if (PORT == 0) {
            Properties properties = PropertiesFileUtils.readProperties(RpcConfigProperties.RPC_CONFIG_PATH.getPropertyValue());
            if (properties != null) {
                String configPort = properties.getProperty(RpcConfigProperties.SERVER_PORT.getPropertyValue());
                PORT = configPort != null ? Integer.parseInt(configPort) : DEFAULT_PORT;
            } else {
                PORT = DEFAULT_PORT;
            }
        }
        return PORT;
    }

    public void registerService(Object service) {
        serviceProvider.publishService(service);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    @SneakyThrows
    public void start() {
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 开启 TCP 的心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 存放 TCP 连接的请求队列的最大长度，如果建立连接较多而导致服务器处理较慢，可以调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 30秒没有收到客户端的请求，就关闭连接
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            pipeline.addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务启动异常：", e);
        } finally {
            log.error("关闭 bossGroup 和 workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 初始化bean的时候执行
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
    }
}
