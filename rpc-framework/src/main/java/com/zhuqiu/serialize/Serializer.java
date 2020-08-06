package com.zhuqiu.serialize;

/**
 * 序列化接口，所有的序列化实现类都要实现这个接口
 *
 * @author zhuqiu
 * @date 2020/8/2
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj   被序列化的对象
     * @return      字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 经过序列化的字节数组
     * @param clazz 目标类
     * @param <T>   目标类的类型，如果不知道类的类型的话，使用 {@code Class<?>}
     * @return      反序列化得到的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
