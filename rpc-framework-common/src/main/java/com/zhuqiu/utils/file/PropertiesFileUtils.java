package com.zhuqiu.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件工具类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class PropertiesFileUtils {

    private PropertiesFileUtils(){}

    public static Properties readPropertiesFile(String filename) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String rpcConfigPath = rootPath + filename;
        Properties properties = null;
        try (FileInputStream fileInputStream = new FileInputStream(rpcConfigPath)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            log.error("读取配置文件时发生异常: [{}]", filename);
        }
        return properties;
    }
}
