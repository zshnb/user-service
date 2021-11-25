package com.zshnb.userservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableTransactionManagement
@MapperScan("com.zshnb.userservice.mapper")
@EnableCaching(proxyTargetClass = true)
public class SpringMainTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMainTestApplication.class, args);
    }
}
