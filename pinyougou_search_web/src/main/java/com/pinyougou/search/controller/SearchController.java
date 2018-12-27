package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/18 0018 15:48
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Reference
    private SearchService searchService;
    @RequestMapping("/searchItem")
    public Map<String, Object> searchItem(@RequestBody Map searchMap){
        return searchService.search(searchMap);
    }
}
