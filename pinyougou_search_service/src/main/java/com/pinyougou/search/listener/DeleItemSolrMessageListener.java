package com.pinyougou.search.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @Author: Liuyu
 * @Date: 2018/12/21 0021 23:14
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class DeleItemSolrMessageListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            //删除索引库
            SolrDataQuery query = new SimpleQuery("item_goodsid:"+goodsId);
            solrTemplate.delete(query);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
