package com.bootscoder.shopping_manager_api.controller;

import com.bootscoder.shopping_common.pojo.Brand;
import com.bootscoder.shopping_common.service.BrandService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;

/**
 * 品牌控制器
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@RestController
@RequestMapping(("/brand"))
public class BrandController {
    @DubboReference
    private BrandService brandService;
    @GetMapping("/findById")
    public Brand findById(Long id){
        return brandService.findById(id);
    }
}
