package com.zhuqiu.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zhuqiu.exception.SerializeException;
import com.zhuqiu.remoting.dto.RpcRequest;
import com.zhuqiu.remoting.dto.RpcResponse;
import com.zhuqiu.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo序列化实现类，序列化效率很高，但是只兼容Java语言
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
@Slf4j
public class KryoSerializer implements Serializer {


    /**
     * Kryo不是线程安全的，因此每个线程应该拥有自己独立的Kryo实例
     * (或 new ThreadLocal<Kryo> Override initialValue 方法)
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
//        kryo.register(RpcRequest.class);
//        kryo.register(RpcResponse.class);
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        // try-with-resources 来打开资源，运行完毕后自动释放资源
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream, 2048*2048)) {
            Kryo kryo = kryoThreadLocal.get();
            // 将Object序列化为byte数组
            kryo.writeClassAndObject(output, obj);
            // 使用完之后进行remove，回收Kryo实例，释放内存空间
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(inputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // 从byte数组中反序列化出目标对象
            Object object = kryo.readClassAndObject(input);
            kryoThreadLocal.remove();
            return clazz.cast(object);
        } catch (Exception e) {
            throw new SerializeException("反序列化失败");
        }
    }
}
