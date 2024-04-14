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
     * @param userId 用户id
     * @return 用户购物车列表
     */
    @GetMapping("/findCartList")
    public BaseResult<List<CartGoods>> findCartList(@RequestHeader Long userId){
        List<CartGoods> cartList = cartService.findCartList(userId);
        return BaseResult.ok(cartList);
    }

    /**
     * 新增商品到购物车
     * @param cartGoods 购物车商品
     * @param userId 令牌中携带的用户id
     * @return 操作结果
     */
    @PostMapping("/addCart")
    public BaseResult addCart(@RequestBody CartGoods cartGoods,@RequestHeader Long userId){
        cartService.addCard(userId, cartGoods);
        return BaseResult.ok();
    }

    /**
     * 修改购物车商品数量
     * @param userId 令牌中携带的用户id
     * @param goodId 商品id
     * @param num 修改后的数量
     * @return 操作结果
     */
    @GetMapping("/handleCart")
    public BaseResult addCart(@RequestHeader Long userId,Long goodId,Integer num){
        cartService.handleCart(userId, goodId, num);
        return BaseResult.ok();
    }

    /**
     * 删除购物车商品
     * @param userId 令牌中携带的用户id
     * @param goodId 商品id
     * @return 操作结果
     */
    @DeleteMapping("/deleteCart")
    public BaseResult deleteCart(@RequestHeader Long userId,Long goodId){
        cartService.deleteCartOption(userId, goodId);
        return BaseResult.ok();
    }
}
