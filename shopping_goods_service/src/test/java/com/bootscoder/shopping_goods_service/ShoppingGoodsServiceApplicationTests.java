package com.bootscoder.shopping_goods_service;

import com.bootscoder.shopping_common.service.GoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingGoodsServiceApplicationTests {

    @Autowired
    GoodsService goodsService;
    @Test
    void contextLoads() {
        System.out.println(goodsService.findAll());
    }

}
