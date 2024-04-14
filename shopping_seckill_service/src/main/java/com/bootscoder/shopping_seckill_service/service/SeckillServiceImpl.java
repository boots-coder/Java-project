package com.bootscoder.shopping_seckill_service.service;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.CartGoods;
import com.bootscoder.shopping_common.pojo.Orders;
import com.bootscoder.shopping_common.pojo.SeckillGoods;
import com.bootscoder.shopping_common.result.BusException;
import com.bootscoder.shopping_common.result.CodeEnum;
import com.bootscoder.shopping_common.service.SeckillService;
import com.bootscoder.shopping_seckill_service.mapper.SeckillGoodsMapper;
import com.bootscoder.shopping_seckill_service.redis.RedissonLock;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private BitMapBloomFilter bitMapBloomFilter;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonLock redissonLock;

    /**
     * 每分钟查询一次数据库，更新redis中的秒杀商品数据
     * 条件为startTime < 当前时间 < endTime,库存大于0
     */
    @Scheduled(cron = "0 * * * * *")
    public void refreshRedis() {
        System.out.println("同步mysql秒杀商品到redis...");
        // 将redis中秒杀商品的库存数据同步到mysql
        List<SeckillGoods> seckillGoodsListOld = redisTemplate.boundHashOps("seckillGoods").values();
        for (SeckillGoods seckillGoods : seckillGoodsListOld) {
            // 从数据库查询秒杀商品
            QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
            queryWrapper.eq("goodsId",seckillGoods.getGoodsId());
            SeckillGoods sqlSeckillGoods = seckillGoodsMapper.selectOne(queryWrapper);
            if (sqlSeckillGoods == null){
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getGoodsId());
            }else {
                // 修改秒杀商品的库存
                sqlSeckillGoods.setStockCount(seckillGoods.getStockCount());
                seckillGoodsMapper.updateById(sqlSeckillGoods);
            }
        }

        // 1.查询数据库中“正在秒杀”的商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        queryWrapper.le("startTime", now) // 当前时间晚于开始时间
                .ge("endTime", now) // 当前时间早于结束时间
                .gt("stockCount", 0); // 库存大于0
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(queryWrapper);
        // 2.删除之前的秒杀商品
        redisTemplate.delete("seckillGoods");

        // 3.保存现在正在秒杀的商品
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(), seckillGoods);
            bitMapBloomFilter.add(seckillGoods.getGoodsId().toString());
        }
    }

    @SentinelResource("findPageByRedis")
    @Override
    public Page<SeckillGoods> findPageByRedis(int page, int size) {
        // 1. 查询所有秒杀商品列表
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        // 2. 获取当前页商品列表
        // 开启截取索引
        int start = (page - 1) * size;
        // 结束截取索引
        int end = start + size > seckillGoodsList.size() ? seckillGoodsList.size() : start + size;
        // 截取当前页的结果集
        List<SeckillGoods> seckillGoods = seckillGoodsList.subList(start, end);
        // 3.构造页面对象
        Page<SeckillGoods> seckillGoodsPage = new Page();
        seckillGoodsPage.setCurrent(page) // 当前页
                .setSize(size) // 每页条数
                .setTotal(seckillGoodsList.size()) // 总条数
                .setRecords(seckillGoods); // 结果集

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return seckillGoodsPage;
    }

    @Override
    public SeckillGoods findSeckillGoodsByRedis(Long goodsId) {
        // 布隆过滤器判断商品是否存在，如果不存在，直接返回空
        if (!bitMapBloomFilter.contains(goodsId.toString())){
            System.out.println("布隆过滤器判断商品不存在");
        }

        // 1. 从redis中查询秒杀商品
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(goodsId);
        // 2. 如果查到商品，返回
        if (seckillGoods != null) {
            System.out.println("从redis中查询秒杀商品");
            return seckillGoods;
        }
        return null;
    }

    @Override
    public Orders createOrder(Orders orders) {
        String lockKey = orders.getCartGoods().get(0).getGoodId().toString();
        if (redissonLock.lock(lockKey,10000)){
            try {
                // 1.生成订单对象
                orders.setId(IdWorker.getIdStr()); // 手动使用雪花算法生成订单id
                orders.setStatus(1); // 订单状态未付款
                orders.setCreateTime(new Date()); // 订单创建时间
                orders.setExpire(new Date(new Date().getTime() + 1000 * 60 * 5)); // 订单过期时间
                // 计算商品价格
                CartGoods cartGoods = orders.getCartGoods().get(0);
                Integer num = cartGoods.getNum();
                BigDecimal price = cartGoods.getPrice();
                BigDecimal sum = price.multiply(BigDecimal.valueOf(num));
                orders.setPayment(sum);

                // 2.减少秒杀商品库存
                // 查询秒杀商品
                SeckillGoods seckillGoods = findSeckillGoodsByRedis(cartGoods.getGoodId());

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 查询库存，库存不足抛出异常
                if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
                    throw new BusException(CodeEnum.NO_STOCK_ERROR);
                }

                // 减少库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() - cartGoods.getNum());
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(), seckillGoods);

                // 3.将订单数据保存到Redis中
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                // 设置订单1分钟过期
                redisTemplate.opsForValue().set(orders.getId(), orders, 100, TimeUnit.MINUTES);
                /**
                 * 给订单创建副本，副本的过期时间长于原订单
                 * redis过期后触发过期事件时，redis的数据已经过期，此时只能拿到key，拿不到value
                 * 而过期事件需要回退商品库存，必须拿到value即订单详情，才能拿到商品数据，进行回退操作
                 * 我们保存一个订单副本，过期时间长于原订单，我们就可以通过副本拿到订单数据
                 */
                redisTemplate.opsForValue().set(orders.getId() + "_copy", orders, 102, TimeUnit.MINUTES);
                System.out.println("下单成功，订单号:"+orders.getId());
                System.out.println("库存还有:"+seckillGoods.getStockCount());
                return orders;
            }finally {
                redissonLock.unlock(lockKey);
            }
        }else {
            return null;
        }


    }

    @Override
    public Orders findOrder(String id) {
        Orders orders = (Orders) redisTemplate.opsForValue().get(id);
        return orders;
    }

    @Override
    public Orders pay(String orderId) {
        // 1.查询订单，设置支付相关数据
        Orders orders = findOrder(orderId);
        if (orders == null) {
            throw new BusException(CodeEnum.ORDER_EXPIRED_ERROR);
        }

        orders.setStatus(2);
        orders.setPaymentTime(new Date());
        orders.setPaymentType(2); // 支付宝支付

        // 2.从redis中删除订单数据
        redisTemplate.delete(orderId);

        // 3.返回订单数据
        return orders;
    }

    @Override
    public void addRedisSeckillGoods(SeckillGoods seckillGoods) {
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(), seckillGoods);
        bitMapBloomFilter.add(seckillGoods.getGoodsId().toString());
    }

    @SentinelResource(value = "findSeckillGoodsByMySql",blockHandler = "mysqlBlockHandler")
    @Override
    public SeckillGoods findSeckillGoodsByMySql(Long goodsId) {
        // 3. 如果没有查到商品，从数据库查询秒杀商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
        queryWrapper.eq("goodsId", goodsId);
        SeckillGoods seckillGoodsMysql = seckillGoodsMapper.selectOne(queryWrapper);
        System.out.println("从mysql中查询秒杀商品");
        // 4. 如果该商品不在秒杀状态，返回null
        Date now = new Date();
        if (seckillGoodsMysql == null
                || now.after(seckillGoodsMysql.getEndTime())
                || now.before(seckillGoodsMysql.getStartTime())
                || seckillGoodsMysql.getStockCount() <= 0){
            return null;
        }
        // 5. 如果该商品在秒杀状态，将商品保存到redis中，并返回该商品
        addRedisSeckillGoods(seckillGoodsMysql);
        return seckillGoodsMysql;
    }

    /**
     * 降级处理
     * @param goodsId
     * @param e
     * @return 空值
     */
    public SeckillGoods mysqlBlockHandler(Long goodsId,BlockException e){
        System.out.println("服务降级方法");
        return null;
    }


}
