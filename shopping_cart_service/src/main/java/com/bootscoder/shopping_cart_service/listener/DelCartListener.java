package com.bootscoder.shopping_cart_service.listener;

import com.bootscoder.shopping_common.service.CartService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "del_cart_queue",consumerGroup = "del_cart_group")
public class DelCartListener implements RocketMQListener<Long> {
    @Autowired
    private CartService cartService;
    @Override
    public void onMessage(Long id) {
        cartService.deleteCartGoods(id);
    }
}
