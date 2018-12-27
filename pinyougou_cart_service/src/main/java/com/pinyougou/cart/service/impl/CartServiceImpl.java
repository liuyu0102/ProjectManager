package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Liuyu
 * @Date: 2018/12/25 0025 17:13
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品到购物车
     * @param cartList 哪个购物车
     * @param itemId 商品sku编号
     * @param num  添加的数量
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
       // 根据商品id查询到该商品关联的商家(sellerId),判断购物车列表中是否存在该商家的购物车,
        //注入查询商品的mapper,获取该商品,根据该商品得到商家id
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //根据商品id查询数据库时没有获取到商品,抛出运行时异常
        if (item == null){
            throw new RuntimeException("商品不存在");
        }

        //判断商品状态不等于1,抛出运行时异常
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品无效");
        }

        String sellerId = item.getSellerId();
        //定义判断购物车列表是否存在该商家的购物车,构建一个方法,传入购物车列表,要添加商品的商家id
        //返回值是该购物车对象Cart
        Cart cart = searchCartBySellerId(cartList,sellerId);
        if (cart == null){//不存在该商家购物车,创建购物车对象
            cart = new Cart();
            //组装数据,商家id  商家名称  商品详情列表及详情对象
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //创建购物车商品详情列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //构建组装商品详情对象的方法
            TbOrderItem orderItem = createOrderItem(item,num);
            //将商品添加到商品列表,商品列表添加到购物车对象,购物车对象添加到购物车列表
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }else {//存在该商家购物车对象
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //判断购物车是否存在要添加的商品
            TbOrderItem orderItem = searchOrderItemByItemId(orderItemList,itemId);
            if (orderItem == null){//购物车不存在该商品
                //添加该商品对象,添加到商品明细列表中
                orderItem = createOrderItem(item,num);
                orderItemList.add(orderItem);
            }else {//购物车存在该商品
                //更改购物车的商品的数量以及购物车商品总金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                if (orderItem.getNum() < 1){//判断该商品数量,小于1移除
                    orderItemList.remove(orderItem);
                }
                if (orderItemList.size() < 1){//判断商品列表是否存在商品,不存在移除商家购物车对象
                    cartList.remove(cart);
                }

            }
        }
        return cartList;
    }

    /**
     * 查询购物车列表
     * @param sessionId
     * @return
     */
    @Override
    public List<Cart> selectCartListFromRedis(String sessionId) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(sessionId).get();
        //判断cartList是否为null 为null返回一个空集合,防止fastJson解析异常
        if (cartList == null){
            return new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将添加商品后的购物车列表存入redis中
     * @param sessionId
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String sessionId, List<Cart> cartList) {
        redisTemplate.boundValueOps(sessionId).set(cartList,7L, TimeUnit.DAYS);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                Integer num = orderItem.getNum();
                Long itemId = orderItem.getItemId();
                cartList_username = addItemToCartList(cartList_username, itemId, num);
            }
        }
        return cartList_username;
    }

    /**
     * 清空登录前购物车列表
     * @param sessionId
     */
    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    /**
     * 判断该商品是否存在于商品明细列表中
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()){
                //购物车存在该商品
                return orderItem;
            }
        }
        return null;

    }

    /**
     * 组装商品明细对象的方法  添加商品
     * @param item  需要item的字段
     * @param num   需要商品的数量
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        //新增商品到购物车时,判断新增数量不能小于1,抛出运行时异常
        if(num < 1){
            throw new RuntimeException("新增商品数量不能小于1");
        }

        TbOrderItem orderItem = new TbOrderItem();
        //  `item_id` bigint(20) NOT NULL COMMENT '商品id',
        //  `goods_id` bigint(20) DEFAULT NULL COMMENT 'SPU_ID',
        //  `title` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品标题',
        //  `price` decimal(20,2) DEFAULT NULL COMMENT '商品单价',
        //  `num` int(10) DEFAULT NULL COMMENT '商品购买数量',
        //  `total_fee` decimal(20,2) DEFAULT NULL COMMENT '商品总金额',
        //  `pic_path` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片地址',
        //  `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL,
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    /**
     * 判断该商家是否存在于购物车列表中
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){//存在该商家购物车
                return cart;
            }
        }
        return null;
    }
}
