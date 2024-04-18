package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Orders;
import com.bootscoder.shopping_common.pojo.SeckillGoods;

public interface SeckillService {
    /**
     * 网站用户从redis查询秒杀商品列表
     * @param page 页码
     * @param size 每页条数
     * @return 秒杀商品列表
     */
    Page<SeckillGoods> findPageByRedis(int page,int size);

    /**
     * 查询秒杀商品详情
     * @param goodsId 秒杀商品id
     * @return 秒杀商品
     */
    SeckillGoods findSeckillGoodsByRedis(Long goodsId);

    /**
     * 生成秒杀订单
     * @param orders 订单数据
     * @return 生成的订单
     */
    Orders createOrder(Orders orders);

    /**
     * 查询秒杀订单详情
     * @param id 订单id
     * @return 订单详情
     */
    Orders findOrder(String id);

    /**
     * 支付秒杀订单
     * @param orderId 订单id
     * @return 订单对象
     */
    Orders pay(String orderId);

    /**
     * 将一个秒杀商品保存到redis中
     * @param seckillGoods 秒杀商品对象
     */
    void addRedisSeckillGoods(SeckillGoods seckillGoods);

    /**
     * 从数据库查询秒杀商品详情
     * @param goodsId 商品id
     * @return 秒杀商品详情
     */
    SeckillGoods findSeckillGoodsByMySql(Long goodsId);


}
