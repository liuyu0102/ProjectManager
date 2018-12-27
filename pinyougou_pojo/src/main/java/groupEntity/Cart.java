package groupEntity;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/25 0025 16:59
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class Cart implements Serializable {
    private String sellerId;
    private String sellerName;
    private List<TbOrderItem> orderItemList;//购物车商品详情列表

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
