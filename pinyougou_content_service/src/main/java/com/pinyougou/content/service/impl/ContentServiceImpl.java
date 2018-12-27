package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        //新增之后,清空新增广告对应的广告分类的缓存
        contentMapper.insert(content);
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        //无论是否修改分类广告的位置,都需要清空最开始的分类广告缓存
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
        contentMapper.updateByPrimaryKey(content);
        //修改后判断是否修改的分类id
        if (content.getCategoryId().longValue() != tbContent.getCategoryId().longValue() ){
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除一个分类下的广告,先获取要删除广告对应的分类广告是什么,清空对应分类广告缓存
            TbContent content = contentMapper.selectByPrimaryKey(id);
            contentMapper.deleteByPrimaryKey(id);
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
    }

    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询广告数据且广告状态为1
     * 创建redis缓冲
     * @param categoryId
     * @return
     */
    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {
        //获取redis中的数据
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        //判断缓存中是否有值
        if (contentList == null){
            //查询mysql
            System.out.println("from mysql .....");
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            //根据分类id  且  状态为1
            criteria.andCategoryIdEqualTo(categoryId);
            criteria.andStatusEqualTo("1");
            contentList = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("content").put(categoryId,contentList);
        }else { //此步仅用于测试,正常开发不必
            //redis
            System.out.println("from redis .....");
        }
        return contentList;
    }

}
