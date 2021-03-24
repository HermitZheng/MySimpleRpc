package com.zhuqiu.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件工具类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class PropertiesFileUtils {

    private static volatile Properties properties;

    private PropertiesFileUtils(){}

    public static Properties readProperties(String filename) {
        if (properties == null) {
            synchronized (PropertiesFileUtils.class) {
                if (properties == null) {
                    properties = readPropertiesFile(filename);
                }
            }
        }
        return properties;
    }

    private static Properties readPropertiesFile(String filename) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(filename)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("读取配置文件时发生异常: [{}]", filename);
        }
        return properties;
    }
}
