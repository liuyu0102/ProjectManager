package com.pinyougou.user.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/10 0010 17:29
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */

/**
 * 安全框架认证服务类  完成认证和授权
 */
public class UserDetailServiceImpl implements UserDetailsService {
    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名(主键)查询对象
        TbSeller seller = sellerService.findOne(username);
        //判断查询到的对象是否为空
        if (seller != null) {
            //判断状态是否审核通过
            if ("1".equals(seller.getStatus())) {
                //2定义权限集合,添加权限根据查看GrantedAuthority源码得知需要new SimpleGrantedAuthority
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                //1.根据查看UserDetails接口 得知返回它的子类User,查看User得知参数含义
                return new User(username, seller.getPassword(), authorities);
            } else {
                return null;
               }
        } else {
            return null;
        }
    }
}
