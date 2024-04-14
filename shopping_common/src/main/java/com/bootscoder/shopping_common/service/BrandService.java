package com.bootscoder.shopping_common.service;

import com.bootscoder.shopping_common.pojo.Brand;

/**
 * 商品服务接口
 *
 * @author bootsCoder
 * @date created on 2024/4/14
 */
public interface BrandService {
    /**
     * 查询品牌
     * @param id
     * @return
     */
    Brand findById(Long id);
}
