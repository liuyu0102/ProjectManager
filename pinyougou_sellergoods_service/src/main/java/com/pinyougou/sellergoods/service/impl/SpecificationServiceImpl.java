package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import entity.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import groupEntity.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/5 0005 19:54
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 关键字搜索分页
     * @param specification
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult search(TbSpecification specification, Integer pageNum, Integer pageSize) {
        //开启分页插件
        PageHelper.startPage(pageNum,pageSize);
        //声明封装对象的查询条件
        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (specification != null){
            String specName = specification.getSpecName();//获取输入的规格名称
            if (specName != null && !"".equals(specName)){
                criteria.andSpecNameLike("%"+specName+"%");
            }
        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加
     * @param specification
     */
    @Override
    public void add(Specification specification) {
        //获取规格实体,插入数据库,添加insert语句返回id的功能,
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);
        //获取规格列表实体,遍历插入数据库
        List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOptions();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
            //将获取的规格id设置给规格选项关联id
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(tbSpecificationOption);
        }

    }

    /**
     * 根据id查询组合实体
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //创建组合实体对象
        Specification specification = new Specification();
        //根据id查询规格对象
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //把规格对象设置给组合实体对象
        specification.setTbSpecification(tbSpecification);
        //获取规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        //由于已知id为规格的id  为规格选项的关联id  所以用条件查询
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setTbSpecificationOptions(tbSpecificationOptions);
        return specification;
    }

    /**
     * 根据组合实体修改
     * @param specification
     */
    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //清空规格选项   再修改
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);
        List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOptions();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
            //将获取的规格id设置给规格选项关联id
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(tbSpecificationOption);
        }

    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);

            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }

    }

    /**
     * 模板查询关联规格
     * @return
     */
    @Override
    public List<Map> selectSpecList() {
        return specificationMapper.selectSpecList();
    }
}
