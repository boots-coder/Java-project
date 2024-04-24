package com.bootscoder.shopping_order_service.service;

import com.bootscoder.shopping_common.pojo.CartGoods;
import com.bootscoder.shopping_common.pojo.Orders;
import com.bootscoder.shopping_common.service.OrdersService;
import com.bootscoder.shopping_order_service.mapper.CartGoodsMapper;
import com.bootscoder.shopping_order_service.mapper.OrdersMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DubboService
public class OrderServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CartGoodsMapper cartGoodsMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    private final String CHECK_ORDERS_QUEUE = "check_orders_queue";

    @Override
    public Orders add(Orders orders) {
        // 订单状态未付款
         if (orders.getStatus() == null){
            orders.setStatus(1);
        }
        // 订单创建时间
        orders.setCreateTime(new Date());
        // 计算订单价格，遍历订单所有商品
        List<CartGoods> cartGoods = orders.getCartGoods();
        BigDecimal sum = BigDecimal.ZERO;
        for (CartGoods cartGood : cartGoods) {
            // 数量
            BigDecimal num = new BigDecimal(cartGood.getNum());
            // 单价
            BigDecimal price = cartGood.getPrice();
            // 数量 * 单价
            BigDecimal multiply = num.multiply(price);
            sum = sum.add(multiply);
        }
        orders.setPayment(sum);
        // 保存订单
        ordersMapper.insert(orders);
        // 保存订单商品
        for (CartGoods cartGood : cartGoods) {
            cartGood.setOrderId(orders.getId());
            cartGoodsMapper.insert(cartGood);
        }

        // 发送延时消息，30分钟后判断订单是否支付
        // 延时等级1~16分别表示 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        rocketMQTemplate.syncSend(CHECK_ORDERS_QUEUE, MessageBuilder.withPayload(orders.getId()).build(),15000,4);
        return orders;
    }

    @Override
    public void update(Orders orders) {
        ordersMapper.updateById(orders);
    }

    @Override
    public Orders findById(String id) {
        return ordersMapper.findById(id);
    }

    @Override
    public List<Orders> findUserOrders(Long userId, Integer status) {
        return ordersMapper.findOrderByUserIdAndStatus(userId, status);
    }
}
