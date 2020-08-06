package com.zhuqiu.remoting.transport.netty.codec;

import com.zhuqiu.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 自定义解码器，负责处理 "入站" Inbound 消息，将消息的字节流转换为需要的业务对象
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;
    private final Class<?> clazz;

    /**
     * 记录字节数组长度的一个 int 占用4个字节，存储在ByteBuf头部
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 解码，将字节数组转换为需要的对象
     *
     * @param ctx   解码器关联的上下文对象
     * @param in    字节数组容器，将要 入站 的字节流ByteBuf对象
     * @param out   返回经过反序列化得到的对象
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 由于消息长度已占4个字节，因此可读取长度必须大于4
        if (in.writableBytes() >= BODY_LENGTH) {
            // 记录当前读指针位置，以便之后重置readerIndex
            in.markReaderIndex();
            // 获取字节数组长度（在Encoder中写入）
            int length = in.readInt();
            // 如果记录的字节数组长度 或者 可读取长度 不合法，则直接return
            if (length < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid!");
                return;
            }
            // 如果可读取字节数小于消息长度，则说明消息不完整，重置读指针
            if (in.readableBytes() < length) {
                in.readerIndex();
                return;
            }
            // 获取消息体字节数组
            byte[] body = new byte[length];
            in.readBytes(body);
            // 将字节数组反序列化为需要的对象
            Object obj = serializer.deserialize(body, clazz);
            out.add(obj);
            log.info("字节数组反序列化成功");
        }
    }
}
