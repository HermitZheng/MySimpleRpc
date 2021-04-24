package com.zhuqiu.remoting.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhuqiu
 * @date 2021/4/24
 */
public class ServerExecutor {

    ExecutorService executorService;

    public ServerExecutor() {
        executorService = Executors.newFixedThreadPool(200);
    }

    public ExecutorService getExecutor() {
        return executorService;
    }
}
