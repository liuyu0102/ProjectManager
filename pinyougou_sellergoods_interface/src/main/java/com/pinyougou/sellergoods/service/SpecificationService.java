package com.pinyougou.sellergoods.service;

import entity.PageResult;
import groupEntity.Specification;
import com.pinyougou.pojo.TbSpecification;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/5 0005 19:54
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public interface SpecificationService {
    PageResult search(TbSpecification specification, Integer pageNum, Integer pageSize);

    void add(Specification specification);

    Specification findOne(Long id);

    void update(Specification specification);

    void delete(Long[] ids);

    List<Map> selectSpecList();
}
