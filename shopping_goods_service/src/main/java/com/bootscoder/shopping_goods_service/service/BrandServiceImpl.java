package com.bootscoder.shopping_goods_service.service;

import com.bootscoder.shopping_common.pojo.Brand;
import com.bootscoder.shopping_common.result.MyException;
import com.bootscoder.shopping_common.result.ResultCode;
import com.bootscoder.shopping_common.service.BrandService;
import com.bootscoder.shopping_goods_service.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Brand service 实现common中的类
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@DubboService
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;
    @Override
    public Brand findById(Long id) {
        return brandMapper.selectById(id);
    }
}
