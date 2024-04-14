package com.bootscoder.shopping_message_service;

import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingMessageServiceApplicationTests {
    @Autowired
    private MessageService messageService;

    @Test
    void contextLoads() {
        BaseResult baseResult = messageService.sendMessage("19935577866", "1234");
        System.out.println(baseResult);
    }

}
