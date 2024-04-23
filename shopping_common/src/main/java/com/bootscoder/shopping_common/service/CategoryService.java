package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Category;

import java.util.List;

/**
 * 广告
 */
public interface CategoryService {
    /**
     * 增加广告
     */
    void add(Category category);
    /**
     * 修改广告
     */
    void update(Category category);
    /**
     * 修过广告状态
     */
    void updateStatus(Long id,Integer status);
    /**
     * 删除广告
     */
    void delete(Long[] ids);
    /**
     * 根据id查询广告
     */
    Category findById(Long id);
    /**
     * 分页查询广告
     */
    Page<Category> search(int page,int size);
    /**
     * 查询广告列表
     */
    List<Category> findAll();
}
