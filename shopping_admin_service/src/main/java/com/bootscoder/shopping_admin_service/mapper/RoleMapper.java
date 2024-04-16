package com.bootscoder.shopping_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootscoder.shopping_common.pojo.Role;
import org.apache.ibatis.annotations.Param;

/**
 * Demo class
 *
 * @author bootsCoder
 * @date created on 2024/4/16
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     *     根据id查询角色，包括权限
     */
    Role findById(Long id);
    /**
     *     删除所有权限
     */
    void deleteRoleAllPermission(Long rid);
    /**
     *     删除中间表
     */
    void deleteRoleAllAdmin(Long rid);
    /**
     *     添加权限
     */
    void addPermissionToRole(@Param("rid") Long rid, @Param("pid")Long pid);
}
