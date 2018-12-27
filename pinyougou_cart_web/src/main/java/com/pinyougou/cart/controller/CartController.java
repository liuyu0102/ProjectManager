package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/26 0026 12:05
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    /**
     * 获取sessionId
     */
    private String getSessionId(){
       //先根据工具查询是否存在保存过的sessionId
        String sessionId = CookieUtil.getCookieValue(request, "cartCookie", "utf-8");
        if (sessionId == null){
            //如果不存在,再根据浏览器HTTPSession获取sessionId
            sessionId = httpSession.getId();
            //将获取到的sessionId基于浏览器保存到后端服务器
            CookieUtil.setCookie(request,response,"cartCookie",sessionId,3600*24*7,"utf-8");
        }
        return sessionId;

    }

    /**
     * 获取购物车列表展示
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取sessionid(调用上面定义好的方法)
        String sessionId = getSessionId();
        //注入服务层,写一个查询购物车列表的方法
        List<Cart> cartList_sessionId = cartService.selectCartListFromRedis(sessionId);
        if ("anonymousUser".equals(username)) {//未登录

            return cartList_sessionId;
        }else {//已登录
            List<Cart> cartList_username = cartService.selectCartListFromRedis(username);
           //判断登录前购物车列表是否为空且集合长度大于0
            if (cartList_sessionId != null && cartList_sessionId.size() > 0){
                //定义service方法,将登录前登录后的购物车列表作为参数传递
                //登录后，需要将登录前的购物车列表数据合并到登录后的购物车列表中。
                cartList_username = cartService.mergeCartList(cartList_sessionId,cartList_username);
                //将合并后的结果，重新放入redis缓存中
                cartService.saveCartListToRedis(username,cartList_username);
                //清除合并前的购物车列表数据
                cartService.deleteCartList(sessionId);
            }
            return cartList_username;
        }

    }

    /**
     * 添加商品到购物车
     */
    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com")
    public Result addItemToCartList(Long itemId, Integer num){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //System.out.println(username);
            //判断是否登录
            // 查询购物车列表(调用上面定义好的方法)
            List<Cart> cartList = findCartList();
            // 调用服务层添加商品到购物车方法
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)){//未登录
                //获取sessionid  用于 存入rides
                String sessionId = getSessionId();
                // 将购物车列表存入redis中
                cartService.saveCartListToRedis(sessionId,cartList);
            }else {//已登录
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加商品到购物车成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(true,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"添加商品到购物车失败");
        }

    }
}
