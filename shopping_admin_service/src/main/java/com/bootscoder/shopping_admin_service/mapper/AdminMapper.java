package com.bootscoder.shopping_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootscoder.shopping_common.pojo.Admin;
import com.bootscoder.shopping_common.pojo.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员的数据库映射类
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * Deletes all roles associated with the specified administrator ID.
     *
     * @param aid the administrator ID
     */
    @Delete("DELETE FROM boots_admin_role WHERE aid = #{aid}")
    void deleteAdminAllRole(@Param("aid") Long aid);
    /**
     * 给管理员添加角色
     */
    @Insert("INSERT INTO boots_admin_role VALUES(#{aid},#{rid});")
    void addRoleToAdmin(@Param("aid") Long aid, @Param("rid") Long rid);
    Admin findById(Long id);
    /**
     *     根据管理员名查询权限
     */
    List<Permission> findAllPermission(String username);
}
