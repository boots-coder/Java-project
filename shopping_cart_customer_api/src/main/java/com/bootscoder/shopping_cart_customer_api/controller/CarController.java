package com.bootscoder.shopping_cart_customer_api.controller;

import com.bootscoder.shopping_common.pojo.CartGoods;
import com.bootscoder.shopping_common.result.BaseResult;
import com.bootscoder.shopping_common.service.CartService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车
 */
@RestController
@RequestMapping("/user/cart")
public class CarController {
    @DubboReference
    private CartService cartService;

    /**
     * 查询用户购物车列表
     * @return 用户购物车列表
     */
    @GetMapping("/findCartList")
    public BaseResult<List<CartGoods>> findCartList(){
        Long userId = 29L;
        List<CartGoods> cartList = cartService.findCartList(userId);
        return BaseResult.ok(cartList);
    }

    /**
     * 新增商品到购物车
     * @param cartGoods 购物车商品
     * @return 操作结果
     */
    @PostMapping("/addCart")
    public BaseResult addCart(@RequestBody CartGoods cartGoods){
        Long userId = 29L;
        cartService.addCard(userId, cartGoods);
        return BaseResult.ok();
    }

    /**
     * 修改购物车商品数量
     * @param goodId 商品id
     * @param num 修改后的数量
     * @return 操作结果
     */
    @GetMapping("/handleCart")
    public BaseResult addCart(Long goodId,Integer num){
        Long userId = 29L;
        cartService.handleCart(userId, goodId, num);
        return BaseResult.ok();
    }

    /**
     * 删除购物车商品
     * @param goodId 商品id
     * @return 操作结果
     */
    @DeleteMapping("/deleteCart")
    public BaseResult deleteCart(Long goodId){
        Long userId = 29L;
        cartService.deleteCartOption(userId, goodId);
        return BaseResult.ok();
    }
}
