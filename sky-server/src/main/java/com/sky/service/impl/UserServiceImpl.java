package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final WeChatProperties weChatProperties;
    private final UserMapper userMapper;
    @Autowired
    public UserServiceImpl(WeChatProperties weChatProperties, UserMapper userMapper) {
        this.weChatProperties = weChatProperties;
        this.userMapper = userMapper;
    }

    /*
    * 微信登录
    * @param UserLoginDTO code 前端传过来的临时登录凭证code
    * @return UserLoginVO(id,openid,token) 返回用户信息和token
    * 流程；
    *  1. 接收参数，并调用微信的接口：https://api.weixin.qq.com/sns/jscode2session，获取到openid
    *  2. 检查是否成功获取到openid，如果没有抛出业务异常
    *  3. 根据openid查询用户表，判断用户是否注册，是否为新用户
    *   1. 如果没有注册，则自动创建新用户，并保存到用户表
    *   2. 如果已经注册，则直接获取用户信息： id
    * */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1.1 调用微信接口，获取openid和session_key
        String code = userLoginDTO.getCode();
        // 1.2 使用HttpClient发送请求，使用Get + 参数的形式
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String,String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        String result = HttpClientUtil.doGet(url, params);
        // 1.3 解析响应结果，获取openid和session_key
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.getString("openid");
        if (openid == null){ // 如果没有获取到openid，说明登录失败
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 2.1 根据openid查询用户表，判断用户是否注册
        User user = userMapper.getByOpenid(openid);
        if (user != null){ // 2.2 如果已经注册，直接返回用户信息
            return user;
        }
        // 2.3 如果没有注册，则自动创建新用户，并保存到用户表
        user = User.builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(user); // insert方法执行后，user对象的id会被回填
        return user;
    }
}
