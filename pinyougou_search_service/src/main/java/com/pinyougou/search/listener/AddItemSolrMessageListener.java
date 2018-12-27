package com.pinyougou.search.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/21 0021 23:09
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class AddItemSolrMessageListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //同步索引库,将上架的商品添加到索引库
            //先查询数据库中的数据,将itemList添加到索引库
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //添加到索引库
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();//没提交也好使了
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
