package com.bootscoder.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Admin;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
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

    @PostMapping("/add")
    public BaseResult add(@RequestBody Admin admin) {
        adminService.add(admin);
        return BaseResult.ok();
    }
    @PutMapping("/update")
    public BaseResult update(@RequestBody Admin admin) {
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
    @GetMapping("/search")
    public BaseResult<Page<Admin>> search(int page, int size) {
        Page<Admin> adminPage = adminService.search(page, size);
        return BaseResult.ok(adminPage);
    }
    @PutMapping("/updateRoleToAdmin")
    public BaseResult updateRoleToAdmin( @RequestParam("aid") Long aid, @RequestParam("rids") Long[] rids) {
        adminService.updateRoleToAdmin(aid,rids);
        return BaseResult.ok();
    }
}