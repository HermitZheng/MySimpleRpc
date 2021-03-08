package com.zhuqiu.time;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author zhuqiu
 * @date 2020/8/6
 */
public interface TimeService {

    /**
     * 获取当前时间
     *
     * @return  String
     */
    String getTime();

    String getTimeByZoneId(ZoneId zone);
}
