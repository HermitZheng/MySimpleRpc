package com.zhuqiu.config;

import com.zhuqiu.registry.zk.utils.CuratorUtils;
import com.zhuqiu.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 在关闭服务时进行一些处理，如注销所有服务
 *
 * @author zhuqiu
 * @date 2020/8/3
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("ShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegister(CuratorUtils.getZkClient());
//            ThreadPoolFactoryUtils.shutdownAllThreadPool();
        }));
    }
}
