package com.bootscoder.shopping_common.service;

import com.bootscoder.shopping_common.pojo.Brand;

/**
 * 商品服务
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
public interface BrandService {
    /**
     * 根据id查询
     */
    Brand findById(Long id);
}
