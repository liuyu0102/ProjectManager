package com.pinyougou.user.service;

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
 * @Date: 2018/12/24 0024 23:12
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //由于cas做认证操作,security只做授权即可
        List<GrantedAuthority> authorities = new ArrayList<>();
        //添加权限
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorities);
    }
}
