package com.bootscoder.shopping_search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootscoder.shopping_common.pojo.*;
import com.bootscoder.shopping_common.service.SearchService;
import com.bootscoder.shopping_search_service.repository.GoodsESRepository;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@DubboService
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ElasticsearchClient client;
    @Autowired
    private GoodsESRepository goodsESRepository;
    @Autowired
    private ElasticsearchTemplate template;

    /**
     * 分词
     *
     * @param text     被分词的文本
     * @param analyzer 分词器
     * @return 分词结果
     */
    @SneakyThrows
    public List<String> analyze(String text, String analyzer) {
        // 创建分词请求
        AnalyzeRequest request = AnalyzeRequest.of(a -> a.index("goods").analyzer(analyzer).text(text));
        // 发送分词请求
        AnalyzeResponse response = client.indices().analyze(request);
        // 处理分词结果
        List<String> words = new ArrayList();
        List<AnalyzeToken> tokens = response.tokens();
        for (AnalyzeToken token : tokens) {
            String term = token.token();
            words.add(term);
        }
        return words;
    }

    // 自动补齐
    @SneakyThrows
    @Override
    public List<String> autoSuggest(String keyword) {
        // 1.自动补齐查询条件
        Suggester suggester = Suggester.of(
                s -> s.suggesters("prefix_suggestion", FieldSuggester.of(
                        fs -> fs.completion(
                                cs -> cs.skipDuplicates(true)
                                        .size(10)
                                        .field("tags")
                        )
                )).text(keyword)
        );
        // 2.自动补齐查询
        SearchResponse<Map> response = client.search(s -> s.index("goods")
                .suggest(suggester), Map.class);

        // 3.处理查询结果
        Map resultMap = response.suggest();
        List<Suggestion> suggestionList = (List) resultMap.get("prefix_suggestion");
        Suggestion suggestion = suggestionList.get(0);
        List<CompletionSuggestOption> resultList = suggestion.completion().options();

        List<String> result = new ArrayList();
        for (CompletionSuggestOption completionSuggestOption : resultList) {
            String text = completionSuggestOption.text();
            result.add(text);
        }

        return result;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        // 1.构造ES搜索条件
        NativeQuery nativeQuery = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(nativeQuery, GoodsES.class);
        // 3.将查询结果封装为Mybatis-plus的Page对象
        // 3.1 将SearchHits对象转为List
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        // 3.2 将List转为Mybatis-plus的Page对象
        Page<GoodsES> page = new Page();
        page.setCurrent(goodsSearchParam.getPage()) // 当前页
                .setSize(goodsSearchParam.getSize()) // 每页条数
                .setTotal(search.getTotalHits()) // 总条数
                .setRecords(content); // 结果集

        // 4.封装查询结果
        GoodsSearchResult result = new GoodsSearchResult();
        // 4.1 封装商品
        result.setGoodsPage(page);
        // 4.2 封装查询参数
        result.setGoodsSearchParam(goodsSearchParam);
        // 4.3 封装查询面板
        buildSearchPanel(goodsSearchParam,result);
        return result;
    }

    /**
     * 封装查询面板，即根据查询条件，找到查询结果关联度前20名的商品进行封装
     * @param goodsSearchParam 查询条件对象
     * @param goodsSearchResult 查询结果对象
     */
    public void buildSearchPanel(GoodsSearchParam goodsSearchParam,GoodsSearchResult goodsSearchResult){
        // 1.构造查询条件
        goodsSearchParam.setPage(1);
        goodsSearchParam.setSize(20);
        goodsSearchParam.setSort(null);
        goodsSearchParam.setSortFiled(null);
        NativeQuery nativeQuery = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(nativeQuery, GoodsES.class);
        // 3.将结果封装为List对象
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }

        // 4.遍历集合，封装查询面板
        // 商品相关的品牌列表
        Set<String> brands = new HashSet();
        // 商品相关的类型列表
        Set<String> productTypes = new HashSet();
        // 商品相关的规格列表
        Map<String,Set<String>> specifications = new HashMap();

        for (GoodsES goodsES : content) {
            // 获取品牌
            brands.add(goodsES.getBrand());
            // 获取类型
            List<String> productType = goodsES.getProductType();
            productTypes.addAll(productType);
            // 获取规格
            Map<String, List<String>> specification = goodsES.getSpecification();
            Set<Map.Entry<String, List<String>>> entries = specification.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                // 规格名
                String key = entry.getKey();
                // 规格值
                List<String> value = entry.getValue();
                // 如果specifications有该规格，则像规格中添加规格项，如果没有该规格，新增键值对
                if (!specifications.containsKey(key)){
                    specifications.put(key,new HashSet(value));
                }else {
                    specifications.get(key).addAll(value);
                }
            }
        }
        goodsSearchResult.setBrands(brands);
        goodsSearchResult.setProductType(productTypes);
        goodsSearchResult.setSpecifications(specifications);
    }


    /**
     * 构造搜索条件
     *
     * @param goodsSearchParam 查询条件对象
     * @return 搜索条件对象
     */
    public NativeQuery buildQuery(GoodsSearchParam goodsSearchParam) {
        // 1.创建复杂查询条件对象
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        BoolQuery.Builder builder = new BoolQuery.Builder();

        // 2.如果查询条件有关键词，关键词可以匹配商品名、副标题、品牌字段；否则查询所有商品
        if (!StringUtils.hasText(goodsSearchParam.getKeyword())){
            MatchAllQuery matchAllQuery = new MatchAllQuery.Builder().build();
            builder.must(matchAllQuery._toQuery());
        }else{
            String keyword = goodsSearchParam.getKeyword();
            MultiMatchQuery keywordQuery = MultiMatchQuery.of(q ->q.query(keyword).fields("goodsName","caption","brand"));
            builder.must(keywordQuery._toQuery());
        }

        // 3.如果查询条件有品牌，则精准匹配品牌
        String brand = goodsSearchParam.getBrand();
        if (StringUtils.hasText(brand)){
            TermQuery brandQuery = TermQuery.of(q -> q.field("brand").value(brand));
            builder.must(brandQuery._toQuery());
        }

        // 4.如果查询条件有价格，则匹配价格
        Double highPrice = goodsSearchParam.getHighPrice();
        Double lowPrice = goodsSearchParam.getLowPrice();
        if (highPrice != null && highPrice != 0){
            RangeQuery lte = RangeQuery.of(q -> q.field("price").lte(JsonData.of(highPrice)));
            builder.must(lte._toQuery());
        }
        if (lowPrice != null && lowPrice != 0){
            RangeQuery gte = RangeQuery.of(q -> q.field("price").gte(JsonData.of(lowPrice)));
            builder.must(gte._toQuery());
        }

        // 5.如果查询条件有规格项，则精准匹配规格项
        Map<String, String> specificationOptions = goodsSearchParam.getSpecificationOption();
        if (specificationOptions != null && specificationOptions.size() > 0){
            Set<Map.Entry<String, String>> entries = specificationOptions.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.hasText(key)){
                    TermQuery termQuery = TermQuery.of(q->q.field("specification."+key+".keyword").value(value));
                    builder.must(termQuery._toQuery());
                }
            }
        }
        nativeQueryBuilder.withQuery(builder.build()._toQuery());

        // 6.添加分页条件
        PageRequest pageable = PageRequest.of(goodsSearchParam.getPage()-1, goodsSearchParam.getSize());
        nativeQueryBuilder.withPageable(pageable);

        // 7.如果查询条件有排序，则添加排序条件
        String sortFiled = goodsSearchParam.getSortFiled();
        String sort = goodsSearchParam.getSort();
        if (StringUtils.hasText(sort) && StringUtils.hasText(sortFiled)){
            Sort sortParam = null;
            // 新品的正序是ID的倒序
            if (sortFiled.equals("NEW")){
                if (sort.equals("ASC")){
                    sortParam = Sort.by(Sort.Direction.DESC,"id");
                }
                if (sort.equals("DESC")){
                    sortParam = Sort.by(Sort.Direction.ASC,"id");
                }
            }
            if (sortFiled.equals("PRICE")){
                if (sort.equals("ASC")){
                    sortParam = Sort.by(Sort.Direction.ASC,"price");
                }
                if (sort.equals("DESC")){
                    sortParam = Sort.by(Sort.Direction.DESC,"price");
                }
            }
            nativeQueryBuilder.withSort(sortParam);
        }

        // 8.返回封装好的搜索条件对象
        return nativeQueryBuilder.build();
    }

    @Override
    public void syncGoodsToES(GoodsDesc goodsDesc) {
        // 将商品详情数据转为GoodsES对象
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        // 商品类型集合
        List<String> productType = new ArrayList();
        productType.add(goodsDesc.getProductType1().getName());
        productType.add(goodsDesc.getProductType2().getName());
        productType.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productType);
        // 商品规格集合
        Map<String, List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        // 遍历规格集合
        for (Specification specification : specifications) {
            // 规格项
            List<SpecificationOption> specificationOptions = specification.getSpecificationOptions();
            // 拿到规格项名
            List<String> optionStrList = new ArrayList();
            for (SpecificationOption option : specificationOptions) {
                optionStrList.add(option.getOptionName());
            }
            map.put(specification.getSpecName(), optionStrList);
        }
        goodsES.setSpecification(map);
        // 关键字
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); // 品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(), "ik_smart")); // 商品名分词后是关键词
        tags.addAll(analyze(goodsDesc.getCaption(), "ik_smart")); // 副标题分词后是关键词
        goodsES.setTags(tags);

        // 将GoodsES对象存入ES
        goodsESRepository.save(goodsES);
    }

    @Override
    public void delete(Long id) {
        goodsESRepository.deleteById(id);
    }
}
