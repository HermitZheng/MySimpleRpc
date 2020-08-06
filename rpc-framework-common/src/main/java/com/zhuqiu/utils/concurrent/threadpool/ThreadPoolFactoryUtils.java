package com.zhuqiu.utils.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 管理线程池的工具类
 *
 * @author zhuqiu
 * @date 2020/8/4
 */
@Slf4j
public class ThreadPoolFactoryUtils {

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtils() {}

    public static void shutdownAllThreadPool() {
        log.info("调用方法shut down所有线程池");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("线程未终止！");
                executorService.shutdown();
            }
        });
    }
}
