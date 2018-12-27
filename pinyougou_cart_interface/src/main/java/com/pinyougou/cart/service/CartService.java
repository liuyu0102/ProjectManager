package com.pinyougou.cart.service;

import groupEntity.Cart;

import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/25 0025 16:51
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public interface CartService {
    /**
     * 添加商品到购物车列表
     * 返回购物车列表
     * @param cartList 哪个购物车
     * @param itemId 商品sku编号
     * @param num  添加的数量
     * @return
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> selectCartListFromRedis(String sessionId);

    void saveCartListToRedis(String sessionId, List<Cart> cartList);

    List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username);

    void deleteCartList(String sessionId);

}
