package com.pinyougou.page.service;

import groupEntity.Goods;

/**
 * @Author: Liuyu
 * @Date: 2018/12/19 0019 19:22
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public interface PageService {
    //查询三张表的数据
    Goods findOne(Long goodsId);
}
