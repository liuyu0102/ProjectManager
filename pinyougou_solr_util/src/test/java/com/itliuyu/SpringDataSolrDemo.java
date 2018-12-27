package com.itliuyu;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.solr.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/16 0016 18:09
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class SpringDataSolrDemo {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private SolrUtil solrUtil;
    @Test
    public void dataImport(){
        solrUtil.dataImport();
    }
    /**
     * 将数据导入索引库
     */
    @Test
    public void addTest(){
        TbItem tbItem = new TbItem();
        tbItem.setId(2L);
        tbItem.setTitle("华为 移动3G 16G 移动3G 32G 移动4G 16G 移动4G 32G");
        tbItem.setBrand("华为2");
        tbItem.setSeller("京东店铺");
        solrTemplate.saveBean(tbItem);
        //必须提交
        solrTemplate.commit();
    }
    /**
     * 查询索引库中的数据
     */
    @Test
    public void queryByIdTest(){
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item.getBrand() +"  "+ item.getSeller()+"  " +item.getId()+"  "+item.getTitle());
    }
    /**
     * 删除索引库中的数据
     */
    @Test
    public void deleteTest(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    /**
     * 批量删除索引库中的数据
     */
    @Test
    public void deleteAllTest(){
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
    /**
     * 批量将数据导入索引库
     */
    @Test
    public void addAllTest(){
        List<TbItem> list = new ArrayList<>();
        for (long i = 1; i <= 100 ; i++) {
            TbItem tbItem = new TbItem();
            tbItem.setId(i);
            tbItem.setTitle(i+"华为 移动3G 16G 移动3G 32G 移动4G 16G 移动4G 32G");
            tbItem.setBrand("华为");
            tbItem.setSeller("京东"+i+"号店铺");
            list.add(tbItem);
        }
        solrTemplate.saveBeans(list);
        //必须提交
        solrTemplate.commit();
    }
    /**
     * 分页查询索引库中的数据
     */
    @Test
    public void queryPageTest(){
        //创建分页查询对象
        Query query = new SimpleQuery("*:*");
        //设置分页条件  分页查询起始值 默认为0 第一条,每页展示条数
        query.setOffset(0);
        query.setRows(5);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //获取总条数,总页数
        System.out.println("总条数"+page.getTotalElements());
        System.out.println("总页数"+page.getTotalPages());
        //获取当前页数据
        List<TbItem> items = page.getContent();
        for (TbItem item : items) {
            System.out.println(item.getBrand() +"  "+ item.getSeller()+"  " +item.getId()+"  "+item.getTitle());
        }
    }
    /**
     * 条件查询索引库中的数据
     */
    @Test
    public void queryMultiTest(){
        //创建分页查询对象
        Query query = new SimpleQuery("*:*");
       //创建查询条件  支持链式编程 参数一:查询字段 .contain
        Criteria criteria = new Criteria("item_title").contains("6").and("item_seller").contains("9");
        //将查询条件传给查询对象
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        //获取总条数,总页数
        System.out.println("总条数"+page.getTotalElements());
        System.out.println("总页数"+page.getTotalPages());
        //获取当前页数据
        List<TbItem> items = page.getContent();
        for (TbItem item : items) {
            System.out.println(item.getBrand() +"  "+ item.getSeller()+"  " +item.getId()+"  "+item.getTitle());
        }
    }
}
