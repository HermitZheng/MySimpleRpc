package com.zhuqiu.rpcspringtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zhuqiu")
public class RpcSpringTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcSpringTestApplication.class, args);
    }

}
