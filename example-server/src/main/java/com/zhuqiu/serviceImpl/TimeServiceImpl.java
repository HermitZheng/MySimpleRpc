package com.zhuqiu.serviceImpl;

import com.zhuqiu.time.Time;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
public class TimeImpl implements Time {

    @Override
    public String getTimt() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        format.format(date);
        String time = format.format(date).toString();
        System.out.println(time);
        return time;
    }
}
