package com.pinyougou.sellergoods.service;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/3 0003 20:34
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public interface BrandService {
    List<TbBrand> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(TbBrand tbBrand);

    void update(TbBrand tbBrand);

    TbBrand findOne(Long id);

    void delete(Long[] ids);

    PageResult search(TbBrand brand, Integer pageNum, Integer pageSize);

    List<Map> selectBrandList();
}
