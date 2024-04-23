package com.bootscoder.shopping_category_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_category_service.mapper.CategoryMapper;
import com.bootscoder.shopping_common.pojo.Category;
import com.bootscoder.shopping_common.service.CategoryService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@DubboService
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
        refreshRedisCategory();
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void delete(Long[] ids) {
        categoryMapper.deleteBatchIds(Arrays.asList(ids));
        refreshRedisCategory();
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Page<Category> search(int page, int size) {
        return categoryMapper.selectPage(new Page(page,size),null);
    }

    @Override
    public List<Category> findAll() {
        // 1.从redis中查询启用的广告
        // 1.1 获取操作redis中list数据的对象
//        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        // 1.2 从redis中获取所有启用的广告
//        List<Category> categoryList = listOperations.range("categories", 0, -1);

//        if (categoryList != null && categoryList.size() > 0){
//            // 2.如果查到结果，直接返回
//            System.out.println("从redis中查询广告");
//            return categoryList;
//        }else {
            // 3.如果redis中没有数据，则从数据库查询所有启用的广告，并同步到redis中
            //todo 这里之后要用log替换
            log.info("从数据库查询广告");
            System.out.println("从数据库中查询广告");
            QueryWrapper<Category> queryWrapper = new QueryWrapper();
            queryWrapper.eq("status",1);
            List<Category> categories = categoryMapper.selectList(queryWrapper);

            // 并同步到redis中
//            listOperations.leftPushAll("categories",categories);
            return categories;
//        }
    }

    /**
     * 更新redis中的广告数据
     */
    public void refreshRedisCategory(){
        // 从数据库查询广告
        QueryWrapper<Category> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status",1);
        List<Category> categories = categoryMapper.selectList(queryWrapper);

        // 删除redis中原有的广告数据
//        redisTemplate.delete("categories");

        // 将新的广告数据同步到redis
//        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
//        listOperations.leftPushAll("categories",categories);
    }
}
