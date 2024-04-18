package com.bootscoder.shopping_admin_service.service;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_admin_service.mapper.AdminMapper;
import com.bootscoder.shopping_common.pojo.Admin;
import com.bootscoder.shopping_common.pojo.Permission;
import com.bootscoder.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员实现类
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@DubboService
@Transactional
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public void add(Admin admin) {
        adminMapper.insert(admin);

    }

    @Override
    public void update(Admin admin) {
        // 如果前端传来空密码，则密码还是原来的密码
        if(!StringUtils.hasText(admin.getPassword())){
            // 查询原来的密码
            String password = adminMapper.selectById(admin.getAid()).getPassword();
            admin.setPassword(password);
        }
        adminMapper.updateById(admin);
    }


    @Override
    public void delete(Long id) {
        adminMapper.deleteAdminAllRole(id);
        adminMapper.deleteById(id);
    }

    @Override
    public Admin findById(Long id) {
        return adminMapper.findById(id);
    }

    @Override
    public Page<Admin> search(int page, int size) {
        return adminMapper.selectPage(new Page<>(page, size), null);
    }
    @Override
    public void updateRoleToAdmin(Long aid, Long[] rids) {
        if (aid == null || aid <= 0) {
            //这个异常我想让我的异常捕获器捕捉到该怎么办呢？
            throw new IllegalArgumentException("Invalid administrator ID");
        }
        // 删除用户的所有角色
        adminMapper.deleteAdminAllRole(aid);
        if (rids != null && rids.length > 0) {
            for (Long rid : rids) {
                // 重新添加管理员角色
                adminMapper.addRoleToAdmin(aid, rid);
            }
        }
    }
    @Override
    public Admin findByAdminName(String username) {
        QueryWrapper<Admin> wrapper = new QueryWrapper();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);
        return admin;
    }
    @Override
    public List<Permission> findAllPermission(String username) {
        return adminMapper.findAllPermission(username);
    }

}
