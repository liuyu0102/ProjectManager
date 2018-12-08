package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import groupEntity.Specification;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/5 0005 19:57
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specificationService;

    /**
     * 根据条件查询分页展示
     * @param specification
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search")
    public PageResult search(@RequestBody TbSpecification specification,Integer pageNum,Integer pageSize){
        return specificationService.search(specification,pageNum,pageSize);
    }
    /**
     * 新增
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification){
        try {
            specificationService.add(specification);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    /**
     * 根据id查询数据
     * @return
     */
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
        return specificationService.findOne(id);
    }
    /**
     * 修改
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification){
        try {
            specificationService.update(specification);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    /**
     * 删除
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    /**
     * 查询模板关联规格
     * @return
     */
    @RequestMapping("/selectSpecList")
    public List<Map> selectSpecList(){
        return specificationService.selectSpecList();
    }
}
