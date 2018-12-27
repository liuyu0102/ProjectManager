package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.*;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/19 0019 19:24
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@Service
@Transactional
public class PageServiceImpl implements PageService {
    //注入三个dao
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Override
    public Goods findOne(Long goodsId) {
        //定义组合实体类
        Goods goods = new Goods();
        //查询goods表的数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);
        String category1Name = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory1Id()).getName();
        String category2Name = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory2Id()).getName();
        String category3Name = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id()).getName();
        Map<String,String> getCategoryMap = new HashMap<>();
        getCategoryMap.put("category1Name",category1Name);
        getCategoryMap.put("category2Name",category2Name);
        getCategoryMap.put("category3Name",category3Name);
        goods.setCategoryMap(getCategoryMap);

        //查询goodsDesc
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);
        //查询item  根据条件查询,因为goodsId在item表中不是主键
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }
}
