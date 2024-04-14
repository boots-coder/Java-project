package com.bootscoder.shopping_goods_service.service;

import com.bootscoder.shopping_common.pojo.Brand;
import com.bootscoder.shopping_common.service.BrandService;
import com.bootscoder.shopping_goods_service.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务类的实现
 *
 * @author bootsCoder
 * @date created on 2024/4/14
 */
@Transactional
@DubboService
public class BrandServiceImpl implements BrandService {
    @Autowired
    BrandMapper brandMapper;
    public Brand findById(Long id){
        return brandMapper.selectById(id);
    }
}
