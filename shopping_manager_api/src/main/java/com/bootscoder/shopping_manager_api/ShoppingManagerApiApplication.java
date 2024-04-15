package com.bootscoder.shopping_manager_api;

import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@RefreshScope// 配置动态刷新
public class ShoppingManagerApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShoppingManagerApiApplication.class, args);
	}

}
