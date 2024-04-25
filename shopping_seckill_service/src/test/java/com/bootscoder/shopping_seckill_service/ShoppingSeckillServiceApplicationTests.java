package com.bootscoder.shopping_seckill_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@SpringBootTest
class ShoppingSeckillServiceApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        Set<String> keys = redisTemplate.keys("17655*");
        System.out.println(keys.size());
        for (String key : keys) {
            System.out.println(key);
        }
    }

}
