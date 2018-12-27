package com.pinyougou.sms.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.sms.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Liuyu
 * @Date: 2018/12/22 0022 20:26
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping(value = "/sms",method = RequestMethod.POST)
public class SmsController {

    @Autowired
    private SmsUtil smsUtil;
    @RequestMapping("/sendSms")
    public Map<String,String> sendSms(String phoneNumbers,String signName,String templateCode,String param){
        try {
            SendSmsResponse response = smsUtil.sendSms(phoneNumbers, signName, templateCode, param);

            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("Code",response.getCode());
            resultMap.put("Message",response.getMessage());
            resultMap.put("RequestId",response.getRequestId());
            resultMap.put("BizId",response.getBizId());
            return resultMap;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}
