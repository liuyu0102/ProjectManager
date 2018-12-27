package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/22 0022 0:01
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class DeleItemPageMessageListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String goodsId = textMessage.getText();
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                new File("E:\\item\\"+item.getId()+".html").delete();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
