package com.bootscoder.shopping_category_customer_api.controller;

import com.bootscoder.shopping_common.pojo.Category;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.List;

/**
 * 广告
 */
@RestController
@RequestMapping("/user/category")
public class CategoryController {
    @DubboReference
    private CategoryService categoryService;

    @GetMapping("/all")
    public BaseResult<List<Category>> findAll(){
        List<Category> categories = categoryService.findAll();
        return BaseResult.ok(categories);
    }
//
//    @GetMapping("/test")
//    public BaseResult test(HttpServletRequest request){
//        Enumeration<String> headerNames = request.getHeaderNames();
//        return BaseResult.ok();
//    }

}
