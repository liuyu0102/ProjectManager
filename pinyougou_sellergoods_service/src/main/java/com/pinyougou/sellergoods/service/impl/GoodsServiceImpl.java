package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//获取货品表
		TbGoods tbGoods = goods.getGoods();
		//设置新增货品状态  新增状态都是0
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);

		TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
		//设置goods_id
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(tbGoodsDesc);

		//插入sku商品表
		if ("1".equals(tbGoods.getIsEnableSpec())){
			List<TbItem> items = goods.getItemList();
			// `title` varchar(100) NOT NULL COMMENT '商品标题',   // 商品名称（SPU名称）+ 商品规格选项名称 中间以空格隔开
			String title = tbGoods.getGoodsName();//{"机身内存":"16G","网络":"联通3G"}
			for (TbItem item : items) {
				String spec = item.getSpec();
				Map<String,String> specMap = JSON.parseObject(spec, Map.class);
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValue(tbGoods, tbGoodsDesc, item);

				itemMapper.insert(item);
			}
		}else {
			//不启用规格
			TbItem item = new TbItem();
			item.setTitle(tbGoods.getGoodsName());
			setItemValue(tbGoods, tbGoodsDesc, item);
			// `spec` varchar(200) DEFAULT NULL,
			item.setSpec("{}");
			// `price` decimal(20,2) NOT NULL COMMENT '商品价格，单位为：元',
			item.setPrice(tbGoods.getPrice());
			// `num` int(10) NOT NULL COMMENT '库存数量',
			item.setNum(9999);
			// `status` varchar(1) NOT NULL COMMENT '商品状态，1-正常，2-下架，3-删除',
			item.setStatus("1");
			// `is_default` varchar(1) DEFAULT NULL,
			item.setIsDefault("1");
			itemMapper.insert(item);
		}



	}
	private void setItemValue(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc, TbItem item) {
		// `image` varchar(2000) DEFAULT NULL COMMENT '商品图片',  // 从 tb_goods_desc item_images中获取第一张
		String itemImages = tbGoodsDesc.getItemImages();
		//{"color":"蓝色","url":"h}  {}
		List<Map> imageList = JSON.parseArray(itemImages, Map.class);
		item.setImage((String)imageList.get(0).get("url"));
		// `categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  //三级分类id
		item.setCategoryid(tbGoods.getCategory3Id());
		// `create_time` datetime NOT NULL COMMENT '创建时间',
		item.setCreateTime(new Date());
		// `update_time` datetime NOT NULL COMMENT '更新时间',
		item.setUpdateTime(new Date());
		// `goods_id` bigint(20) DEFAULT NULL,
		item.setGoodsId(tbGoods.getId());
		// `seller_id` varchar(30) DEFAULT NULL,
		item.setSellerId(tbGoods.getSellerId());
		//以下字段作用：
		// `category` varchar(200) DEFAULT NULL, //三级分类名称
		//TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		item.setCategory(itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
		// `brand` varchar(100) DEFAULT NULL,//品牌名称
		//TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		item.setBrand(brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName());
		// `seller` varchar(200) DEFAULT NULL,//商家店铺名称
		//TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		item.setSeller(sellerMapper.selectByPrimaryKey(tbGoods.getSellerId()).getNickName());
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				/*criteria.andSellerIdLike("%"+goods.getSellerId()+"%");*/
				//由于商家id是主键,要等值查询
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 批量审核
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination addItemSolrTextDestination;
	@Autowired
	private Destination deleItemSolrTextDestination;
	@Autowired
	private Destination addItemPageTextDestination;
	@Autowired
	private Destination deleItemPageTextDestination;
	/**
	 * 批量上架
	 * @param ids
	 * @param isMarketable
	 */
	@Override
	public void updateIsMarketable(Long[] ids, String isMarketable) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			//只有审核通过才能上下架
			if ("1".equals(tbGoods.getAuditStatus())){
				if ("1".equals(isMarketable)){
					//上架  同步商品数据添加到索引库
					jmsTemplate.send(addItemSolrTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});

					//上架  生成静态页
					jmsTemplate.send(addItemPageTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}
				if ("0".equals(isMarketable)){
					//下架   同步商品数据 删除索引库中的数据
					jmsTemplate.send(deleItemSolrTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
					//下架   同步商品数据 删除静态页
					jmsTemplate.send(deleItemPageTextDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}
				tbGoods.setIsMarketable(isMarketable);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}else {
				throw new RuntimeException("只有审核通过才能上下架");
			}

		}
	}

}
