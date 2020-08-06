package com.zhuqiu.remoting.transport.netty.codec;

import com.zhuqiu.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义编码器，负责处理 "出站" Outbound 消息，将消息转化为字节数组，并写入到字节的容器 ByteBuf 对象中。
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;
    private final Class<?> clazz;


    /**
     * 编码，将对象转换为字节码，并写入ByteBuf中
     * @param ctx   编码器关联的上下文对象
     * @param msg   消息体对象，即将要 出站 的对象
     * @param out   字节流容器
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (clazz.isInstance(msg)) {
            // 将消息体转换为字节数组
            byte[] body = serializer.serialize(msg);
            // 获取字节数组长度，此时一个int占用了4个字节，因此 writeIndex + 4 才是消息体内容
            int length = body.length;
            // 写入消息体对应的字节数组长度
            out.writeInt(length);
            // 写入消息体的字节数组
            out.writeBytes(body);

            log.info("对象序列化成功");
        }
    }
}
