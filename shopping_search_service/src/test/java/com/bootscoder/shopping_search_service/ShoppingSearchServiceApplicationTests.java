package com.bootscoder.shopping_search_service;

import com.bootscoder.shopping_common.pojo.GoodsDesc;
import com.bootscoder.shopping_common.service.GoodsService;
import com.bootscoder.shopping_search_service.service.SearchServiceImpl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShoppingSearchServiceApplicationTests {
    @Autowired
    private SearchServiceImpl searchService;
//    @DubboReference
//    private GoodsService goodsService;


    @Test
    void contextLoads() {
        List<String> analyze = searchService.analyze("我爱敲代码，我爱读书", "ik_pinyin");
        System.out.println(analyze);
    }
//    @Test
//    void testSyncGoodsToES(){
//        List<GoodsDesc> goods = goodsService.findAll();
//        for (GoodsDesc goodsDesc : goods) {
//            // 如果商品是上架状态
//            if (goodsDesc.getIsMarketable()){
//                searchService.syncGoodsToES(goodsDesc);
//            }
//        }
//    }
}
