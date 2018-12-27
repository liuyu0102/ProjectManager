package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/19 0019 19:56
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping("/page")
public class PageController {
    @Reference
    private PageService pageService;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @RequestMapping("/genHtml")
    public String genHtml(Long goodsId){
        try {
            //第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是
            Configuration configuration = freeMarkerConfig.getConfiguration();
//        第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("item.ftl");
//        第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
            Goods goods = pageService.findOne(goodsId);
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                Map<String,Object> map = new HashMap<>();
                map.put("goods",goods);
                map.put("item",item);
                //第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
                Writer out  = new FileWriter("E:\\item\\"+item.getId()+".html");
                //第七步：调用模板对象的 process 方法输出文件。
                template.process(map,out);
                //第八步：关闭流
                out.close();
            }
            return "success..................";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed..................";
        }
    }
}
