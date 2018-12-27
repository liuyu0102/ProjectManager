package com.pinyougou.search.service;

/**
 * @Author: Liuyu
 * @Date: 2018/12/18 0018 15:22
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */

import java.util.Map;

/**
 * 搜索服务层接口
 */
public interface SearchService {
    //分析:返回值可以包括页面数据列表,页面数据总量,当前页码,每页展示条数,所以返回值定义map响应
    //      参数: 关键字,品牌,规格,分页数据 ... 所以参数也用map接收
    Map<String,Object> search(Map searchMap);
}
