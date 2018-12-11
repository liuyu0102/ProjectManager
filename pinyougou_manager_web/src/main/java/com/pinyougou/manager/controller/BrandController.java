package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/3 0003 20:40
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RequestMapping("/brand")
@RestController  //@RequestBody 和 @Controller 的结合体
public class BrandController {
    @Reference  //dubbo的注入注解
    private BrandService brandService;

    /**
     * 搜索条件查询分页展示
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand brand, Integer pageNum, Integer pageSize){
        return brandService.search(brand,pageNum,pageSize);
    }
    /**
     * 查询所有品牌数据
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }
    /**
     * 分页
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum,Integer pageSize){
        return brandService.findPage(pageNum,pageSize);
    }

    /**
     * 新增
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
    /**
     * 根据id查询数据
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }


    /**
     * 修改
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
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
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 查询模板关联品牌
     * @return
     */
    @RequestMapping("/selectBrandList")
    public List<Map> selectBrandList(){
       return brandService.selectBrandList();
    }
}
