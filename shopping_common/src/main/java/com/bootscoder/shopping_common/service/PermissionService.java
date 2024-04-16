package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Permission;

import java.util.List;

/**
 * Demo class
 *
 * @author bootsCoder
 * @date created on 2024/4/16
 */
public interface PermissionService {
    public void add(Permission permission);


    public void update(Permission permission);

    public void delete(Long id);


    public Permission findById(Long id);


    public Page<Permission> search(int page, int size);


    public List<Permission> findAll();

}
