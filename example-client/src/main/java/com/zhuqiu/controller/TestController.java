package com.zhuqiu.controller;

import com.zhuqiu.annotation.RpcProxy;
import com.zhuqiu.service.TimeServiceImpl;
import com.zhuqiu.time.TimeService;
import org.springframework.stereotype.Controller;

/**
 * @author zhuqiu
 * @date 2021/1/21
 */
@Controller
public class TestController {

    @RpcProxy(degradation = TimeServiceImpl.class)
    public TimeService timeService;

    public void testTime() {
        String time = timeService.getTime();
        System.out.println(time);
    }
}
