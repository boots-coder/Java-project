package com.bootscoder.shopping_manager_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient // 向注册中心注册服务
@RefreshScope // 配置动态刷新
public class ShoppingManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingManagerApiApplication.class, args);
    }

}
