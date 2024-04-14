package com.bootscoder.shopping_common.service;

import com.bootscoder.shopping_common.pojo.GoodsDesc;
import com.bootscoder.shopping_common.pojo.GoodsSearchParam;
import com.bootscoder.shopping_common.pojo.GoodsSearchResult;

import java.util.List;

public interface SearchService {
    /**
     * 自动补齐关键字
     * @param keyword 被补齐的关键字
     * @return 补齐的关键字集合
     */
    List<String> autoSuggest(String keyword);

    /**
     * 搜索商品
     * @param goodsSearchParam 搜索条件对象
     * @return 搜索结果对象
     */
    GoodsSearchResult search(GoodsSearchParam goodsSearchParam);

    /**
     * 将数据库商品同步到ES
     * @param goodsDesc 商品详情对象
     */
    void syncGoodsToES(GoodsDesc goodsDesc);

    /**
     * 删除ES中的商品
     * @param id 商品id
     */
    void delete(Long id);
}
