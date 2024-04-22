package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.SeckillGoods;

public interface SeckillGoodsService {
    /**
     *     添加秒杀商品
     */
    void add(SeckillGoods seckillGoods);
    /**
     *     修改秒杀商品
     */
    void update(SeckillGoods seckillGoods);
    /**
     *     分页秒杀商品
     */
    Page<SeckillGoods> findPage(int page,int size);

}
