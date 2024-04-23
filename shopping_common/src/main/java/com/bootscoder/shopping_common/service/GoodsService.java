package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Goods;
import com.bootscoder.shopping_common.pojo.GoodsDesc;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品服务
 */
public interface GoodsService {
    /**
     *     新增商品
     */
    void add(Goods goods);
    /**
     *     修改商品
     */
    void update(Goods goods);
    /**
     *     根据id查找商品
     */
    Goods findById(Long id);
    /**
     *     上下架子商品
     */
    void putAway(Long id,Boolean isMarketable);
    /**
     *     分页查寻商品
     */
    Page<Goods> search(Goods goods,int page,int size);
    /**
     *     查询全部商品
     */
    List<GoodsDesc> findAll();
    /**
     *     查询商品详情
     */
    GoodsDesc findDesc(Long id);
}
