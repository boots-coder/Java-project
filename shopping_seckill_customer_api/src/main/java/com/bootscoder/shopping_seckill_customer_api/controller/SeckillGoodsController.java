package com.bootscoder.shopping_seckill_customer_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Orders;
import com.bootscoder.shopping_common.pojo.SeckillGoods;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.OrdersService;
import com.bootscoder.shopping_common.service.SeckillGoodsService;
import com.bootscoder.shopping_common.service.SeckillService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀商品
 */
@RestController
@RequestMapping("/user/seckillGoods")
public class SeckillGoodsController {
    @DubboReference
    private SeckillService seckillService;
    @DubboReference
    private OrdersService ordersService;
    @DubboReference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 用户分页查询秒杀商品
     *
     * @param page 页码
     * @param size 每页条数
     * @return 查询结果
     */
    @GetMapping("/findPage")
    public BaseResult<Page<SeckillGoods>> findPage(int page, int size) {
        Page<SeckillGoods> seckillGoodsPage = seckillService.findPageByRedis(page, size);
        return BaseResult.ok(seckillGoodsPage);
    }

    /**
     * 用户查询秒杀商品详情
     * @param id 商品id
     * @return 查询结果
     */
    @GetMapping("/findById")
    public BaseResult<SeckillGoods> findById(Long id) {
        // 从redis中查询秒杀商品详情
        SeckillGoods seckillGoods = seckillService.findSeckillGoodsByRedis(id);
        if (seckillGoods != null){
            return BaseResult.ok(seckillGoods);
        }else {
            // 如果redis中查找不到，再从数据库查询秒杀商品详情
            SeckillGoods seckillGoodsByMySql = seckillService.findSeckillGoodsByMySql(id);
            return BaseResult.ok(seckillGoodsByMySql);
        }
    }

    /**
     * 创建秒杀订单
     *
     * @param orders 订单对象
     * @return 创建的订单对象
     */
    @PostMapping("/add")
    public BaseResult<Orders> add(@RequestBody Orders orders) {
        Long userId = 29L;
        orders.setUserId(userId);
        Orders order = seckillService.createOrder(orders);
        return BaseResult.ok(order);
    }

    /**
     * 根据id查询秒杀订单
     *
     * @param id 订单id
     * @return 查询结果
     */
    @GetMapping("/findOrder")
    public BaseResult<Orders> findOrder(String id) {
        Orders orders = seckillService.findOrder(id);
        return BaseResult.ok(orders);
    }

    /**
     * 支付秒杀订单
     *
     * @param id 订单id
     * @return 执行结果
     */
    @GetMapping("/pay")
    public BaseResult pay(String id) {
        // 支付秒杀订单
        Orders orders = seckillService.pay(id);
        // 将订单数据存入数据库
        ordersService.add(orders);
        return BaseResult.ok();
    }
}
