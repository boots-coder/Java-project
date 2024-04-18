package com.bootscoder.shopping_manager_api.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Admin;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员api
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @DubboReference
    private AdminService adminService;
    @Autowired
    private PasswordEncoder encoder;
    /**
     * 新增管理员
     * @param admin 管理员对象
     * @return 执行结果
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Admin admin) {
        String password = encoder.encode(admin.getPassword());
        admin.setPassword(password);
        adminService.add(admin);
        return BaseResult.ok();
    }
    /**
     * 修改管理员
     * @param admin 管理员对象
     * @return 执行结果
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Admin admin) {
        String password = admin.getPassword();
        // 密码不为空加密
        if (StringUtils.hasText(password)){
            password = encoder.encode(password);
            admin.setPassword(password);
        }
        adminService.update(admin);
        return BaseResult.ok();
    }
    @DeleteMapping("/delete")
    public BaseResult delete(@RequestParam("aid") Long id) {
        adminService.delete(id);
        return BaseResult.ok();
    }
    @GetMapping("/findById")
    public BaseResult<Admin> findById(@RequestParam("aid") Long id) {
        Admin admin = adminService.findById(id);
        return BaseResult.ok(admin);
    }
    /**
     * 分页查询管理员
     * @param page 页码
     * @param size 每天条数
     * @return 查询结果
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('/admin/search')")
    public BaseResult<Page<Admin>> search(int page, int size){
        Page<Admin> adminPage = adminService.search(page, size);
        return BaseResult.ok(adminPage);
    }
    @PutMapping("/updateRoleToAdmin")
    public BaseResult updateRoleToAdmin( @RequestParam("aid") Long aid, @RequestParam("rids") Long[] rids) {
        adminService.updateRoleToAdmin(aid,rids);
        return BaseResult.ok();
    }
    /**
     * 获取登录管理员名
     *
     * @return 管理员名
     */
    @GetMapping("/getUsername")
    public BaseResult<String> getUsername() {
        // 1.获取会话对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 2.获取认证对象
        Authentication authentication = context.getAuthentication();
        // 3.获取登录用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return BaseResult.ok(username);
    }

}