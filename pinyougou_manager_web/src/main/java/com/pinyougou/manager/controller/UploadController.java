package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * @Author: Liuyu
 * @Date: 2018/12/12 0012 15:02
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    //将ip地址注入
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        //使用工具类
        try {
            //获取文件扩展名,先获取文件名
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            String filePath = FILE_SERVER_URL+path;
            return new Result(true,filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
