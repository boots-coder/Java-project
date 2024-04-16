package com.bootscoder.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Admin;

/**
 * 管理员服务
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
public interface AdminService {
    /**
     * 新增管理员
     */
    void add(Admin admin);
    /**
     * 更新管理员
     */
    void update(Admin admin);
    /**
     * 删除管理员
     */
    void delete(Long id);
    /**
     * 查找管理员
     */
    Admin findById(Long id);
    /**
     * 新增管理员
     */
    Page<Admin> search(int page, int size);
    /**
     * 更新管理员角色
     */
    void updateRoleToAdmin(Long aid, Long[] rids);
}
