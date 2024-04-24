package com.bootscoder.shopping_goods_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.*;
import com.bootscoder.shopping_common.service.GoodsService;
import com.bootscoder.shopping_common.service.SearchService;
import com.bootscoder.shopping_goods_service.mapper.GoodsImageMapper;
import com.bootscoder.shopping_goods_service.mapper.GoodsMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@DubboService
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsImageMapper goodsImageMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate; // RocketMQ工具类


    // 同步商品数据主题
    private final String SYNC_GOOD_QUEUE = "sync_goods_queue";
    // 删除商品数据主题
    private final String DEL_GOOD_QUEUE = "del_goods_queue";


    @Override
    public void add(Goods goods) {
        // 插入商品数据
        goodsMapper.insert(goods);
        // 插入图片数据
        Long goodsId = goods.getId(); // 获取商品主键
        List<GoodsImage> images = goods.getImages(); // 商品图片集合
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId); // 给图片设置商品id
            goodsImageMapper.insert(image); // 插入图片
        }
        // 插入商品_规格项数据
        // 1.获取规格
        List<Specification> specifications = goods.getSpecifications();
        // 2.获取规格项
        List<SpecificationOption> options = new ArrayList();
        // 遍历规格，获取规格中的所有规格项
        for (Specification specification : specifications) {
            options.addAll(specification.getSpecificationOptions());
        }
        // 遍历规格项，插入商品_规格项数据
        for (SpecificationOption option : options) {
            goodsMapper.addGoodsSpecificationOption(goodsId,option.getId());
        }


        // 将商品数据同步到ES中
        GoodsDesc goodsDesc = findDesc(goodsId);
        rocketMQTemplate.syncSend(SYNC_GOOD_QUEUE,goodsDesc);
    }


    @Override
    public void update(Goods goods) {
        // 删除旧图片数据
        Long goodsId = goods.getId(); // 获取商品主键
        QueryWrapper<GoodsImage> queryWrapper = new QueryWrapper();
        queryWrapper.eq("goodsId",goodsId);
        goodsImageMapper.delete(queryWrapper);
        // 删除旧规格数据
        goodsMapper.deleteGoodsSpecificationOption(goodsId);


        // 修改商品数据
        goodsMapper.updateById(goods);
        // 插入图片数据
        List<GoodsImage> images = goods.getImages(); // 商品图片集合
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId); // 给图片设置商品id
            goodsImageMapper.insert(image); // 插入图片
        }
        // 插入商品_规格项数据
        // 1.获取规格
        List<Specification> specifications = goods.getSpecifications();
        // 2.获取规格项
        List<SpecificationOption> options = new ArrayList();
        // 遍历规格，获取规格中的所有规格项
        for (Specification specification : specifications) {
            options.addAll(specification.getSpecificationOptions());
        }
        // 遍历规格项，插入商品_规格项数据
        for (SpecificationOption option : options) {
            goodsMapper.addGoodsSpecificationOption(goodsId,option.getId());
        }


        // 将商品数据同步到ES中
        GoodsDesc goodsDesc = findDesc(goodsId);
        rocketMQTemplate.syncSend(SYNC_GOOD_QUEUE,goodsDesc);
    }


    @Override
    public Goods findById(Long id) {
        return goodsMapper.findById(id);
    }


    @Override
    public void putAway(Long id, Boolean isMarketable) {
        goodsMapper.putAway(id,isMarketable);
        // 上架时数据同步到ES，下架时删除ES数据
        if (isMarketable){
            // 将商品数据同步到ES中
            GoodsDesc goodsDesc = findDesc(id);
            rocketMQTemplate.syncSend(SYNC_GOOD_QUEUE,goodsDesc);
        }else {
            rocketMQTemplate.syncSend(DEL_GOOD_QUEUE,id);
        }
    }


    @Override
    public Page<Goods> search(Goods goods, int page, int size) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper();
        // 判断商品名不为空
        if (goods != null && StringUtils.hasText(goods.getGoodsName())){
            queryWrapper.like("goodsName",goods.getGoodsName());
        }
        Page page1 = goodsMapper.selectPage(new Page(page, size), queryWrapper);
        return page1;
    }


    @Override
    public List<GoodsDesc> findAll() {
        return goodsMapper.findAll();
    }


    @Override
    public GoodsDesc findDesc(Long id) {
        return goodsMapper.findDesc(id);
    }
}
