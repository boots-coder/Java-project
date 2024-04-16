package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Role;

import java.util.List;

/**
 * 角色相关的接口
 *
 * @author bootsCoder
 * @date created on 2024/4/16
 */
public interface RoleService {
    /**
     *   新增角色
     */

    void add(Role role);
    /**
     *  修改角色
     */
    void update(Role role);
    /**
     *   删除角色
     */
    void delete(Long id);
    /**
     *   查询角色
     */
    Role findById(Long id);
    /**
     *   查询所有角色
     */
    List<Role> findAll();
    /**
     *   分页查询角色
     */
    Page<Role> search(int page, int size);
     /**
     *   更新角色
     */
    void updatePermissionToRole(Long rid, Long[] pids);
}
