package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/17 0017 22:10
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    public void dataImport(){
        //获取数据库数据
       List<TbItem> itemList =  itemMapper.findAllGrounding();
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            tbItem.setMapSpec(specMap);
        }
        //将数据导入索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("dataImport finish ... " );
    }

}
