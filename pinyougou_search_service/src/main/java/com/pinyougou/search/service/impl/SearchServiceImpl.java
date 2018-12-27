package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/18 0018 15:29
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@Service
@Transactional
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //1.关键字搜索
        //获取查询关键字
        String keywords = (String) searchMap.get("keywords");
        //创建查询条件
        Criteria criteria = null;
        //判断查询关键字是否为空
        if (keywords != null && !"".equals(keywords)){
            //关键字不为空
            criteria = new Criteria("item_keywords").is(keywords);
        }else {
            //关键字为空 查询所有
            criteria = new Criteria().expression("*:*");
        }


        //创建查询对象
        HighlightQuery query = new SimpleHighlightQuery(criteria);

        //2.分类条件过滤查询
        //获取查询的条件
        String category = (String) searchMap.get("category");
        if (category != null && !"".equals(category)){
            //设置查询条件
            Criteria categoryCriteria = new Criteria("item_category").is(category);
            //创建过滤查询条件
            FilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }
        //3.品牌条件过滤查询
        //获取查询的条件
        String brand = (String) searchMap.get("brand");
        if (brand != null && !"".equals(brand)){
            //设置查询条件
            Criteria brandCriteria = new Criteria("item_brand").is(brand);
            //创建过滤查询条件
            FilterQuery filterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.规格条件过滤查询
        //获取查询的条件
        Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
        if (specMap != null){
            for (String key : specMap.keySet()){
                //设置查询条件
                Criteria specCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                //创建过滤查询条件
                FilterQuery filterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //5.价格区间条件过滤查询
        //获取查询的条件
        String price = (String) searchMap.get("price");
        if (price != null && !"".equals(price)){
            //"0-500" "500-1000" "1000-*" 截取字符串得到起始值和结束值
            String[] prices = price.split("-"); //prices[0]起始值  prices[1]结束值
            //通过判断临界值来设定区间,设定起始值不等于0 查询大于等于起始值
            //                          结束值不等于*  查询小于等于结束值的
            if (!"0".equals(prices[0])){
                //设置查询条件
                Criteria priceCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                //创建过滤查询条件
                FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(prices[1])){
                //设置查询条件
                Criteria priceCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                //创建过滤查询条件
                FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6.新品和价格排序
        //获取查询的条件
        String sortField = (String) searchMap.get("sortField");
        String sort = (String) searchMap.get("sort");
        if (sortField != null && !"".equals(sortField)){
            if ("ASC".equals(sort)){
                query.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
            }else {
                query.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
            }
        }
        //7.分页展示
        //获取查询的条件
        Integer pageNo = (Integer) searchMap.get("pageNo"); //当前页
        Integer pageSize = (Integer) searchMap.get("pageSize"); //每页展示条数
        query.setOffset((pageNo-1)*pageSize);//起始索引
        query.setRows(pageSize);



        //创建高亮对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮字段
        highlightOptions.addField("item_title");
        //设置高亮前后缀
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        //查询对象调用设置高亮的方法
        query.setHighlightOptions(highlightOptions);

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取总条数,总页数
        System.out.println("TotalElements:"+page.getTotalElements());
        System.out.println("TotalPages:"+page.getTotalPages());
        //获取当前页数据
        List<TbItem> content = page.getContent();
        //遍历得到每个商品,将高亮字段赋值给商品的标题title
        for (TbItem item : content) {
            //得到page中的高亮集合
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);
            //判断高亮集合是否为空
            if (highlights != null && highlights.size() > 0){
                //得到集合第一个元素的高亮字段
                List<String> snipplets = highlights.get(0).getSnipplets();
                //判断是否为空,赋值给item的标题
                if (snipplets != null && snipplets.size() > 0){
                    item.setTitle(snipplets.get(0));
                }
            }
        }
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("rows",content);
        resultMap.put("pageNo",pageNo);//当前页
        resultMap.put("totalPages",page.getTotalPages());//总页数
        return resultMap;
    }
}
