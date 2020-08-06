package com.zhuqiu.serviceImpl;

import com.zhuqiu.annotation.RpcService;
import com.zhuqiu.time.TimeService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
@RpcService
public class TimeServiceImpl implements TimeService {

    @Override
    public String getTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        format.format(date);
        String time = format.format(date).toString();
        System.out.println(time);
        return time;
    }
}
