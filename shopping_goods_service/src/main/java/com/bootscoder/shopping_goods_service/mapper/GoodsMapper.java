package com.bootscoder.shopping_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootscoder.shopping_common.pojo.Goods;
import com.bootscoder.shopping_common.pojo.GoodsDesc;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     *     添加商品规格
     */
    void addGoodsSpecificationOption(@Param("gid") Long gid,@Param("optionId") Long optionId);
    /**
     *     删除规格
     */
    void deleteGoodsSpecificationOption(Long gid);
    /**
     *     上下架
     */
    void putAway(@Param("id") Long id,@Param("isMarketable")Boolean isMarketable);
    /**
     *     根据id查询商品详情
     */
    Goods findById(Long id);
    /**
     *     查询所有商品详情
     */
    List<GoodsDesc> findAll();
    /**
     *     查询商品详情
     */
    GoodsDesc findDesc(Long id);
}
