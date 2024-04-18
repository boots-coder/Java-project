package com.bootscoder.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.Brand;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.result.MyException;
import com.bootscoder.shopping_common.result.ResultCode;
import com.bootscoder.shopping_common.service.BrandService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.List;

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
    public BaseResult<Brand> findById(Long id){

        /**
         * 模拟异常
         */
        if (id == 0){
            long i = 1/id;
        } else if (id == -1 ) {
            throw new MyException(ResultCode.PARAMETER_ERROR);
        }
        Brand brand = brandService.findById(id);
        return BaseResult.ok(brand);
    }

    /**
     * 查询所有品牌
     *
     * @return 所有品牌
     */
    @GetMapping("/all")
    public BaseResult<List<Brand>> findAll() {
        List<Brand> brands = brandService.findAll();
        return BaseResult.ok(brands);
    }

    /**
     * 新增品牌
     *
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Brand brand) {
        brandService.add(brand);
        return BaseResult.ok();
    }

    /**
     * 修改品牌
     *
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Brand brand) {
        brandService.update(brand);
        return BaseResult.ok();
    }

    /**
     * 删除品牌
     *
     * @param id 品牌id
     * @return 执行结果
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long id) {
        brandService.delete(id);
        return BaseResult.ok();
    }

    /**
     * 分页查询品牌
     *
     * @param brand 查询条件对象
     * @param page  页码
     * @param size  每页条数
     * @return 查询结果
     */
    @GetMapping("/search")
    public BaseResult<Page<Brand>> search(Brand brand, int page, int size) {
        Page<Brand> page1 = brandService.search(brand, page, size);
        return BaseResult.ok(page1);
    }
}
