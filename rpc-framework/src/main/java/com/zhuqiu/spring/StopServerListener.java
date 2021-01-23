package com.zhuqiu.spring;

import com.zhuqiu.registry.zk.utils.CuratorUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author zhuqiu
 * @date 2021/1/23
 */
@Component
public class StopServerListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        CuratorUtils.clearRegister(CuratorUtils.getZkClient());
    }
}
