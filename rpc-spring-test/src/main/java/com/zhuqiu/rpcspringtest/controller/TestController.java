package com.zhuqiu.rpcspringtest.controller;

import com.zhuqiu.annotation.RpcProxy;
import com.zhuqiu.time.TimeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author zhengqi
 * @Description
 * @Date 11/10/20 5:40 PM
 */
@Controller
@RequestMapping("/rpc")
public class TestController {

    @RpcProxy
    TimeService timeService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        String time = timeService.getTime();
        return time + " Remote Procedure Call Success ";
    }
}
