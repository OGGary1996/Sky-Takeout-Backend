package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

public interface UserService {

    /*
    * 微信登录
    * @param UserLoginDTO code 前端传过来的临时登录凭证code
    * @return UserLoginVO(id,openid,token) 返回用户信息和token
    * 流程；
    *  1. 接收参数，并调用微信的接口：https://api.weixin.qq.com/sns/jscode2session，获取到openid和session_key
    *  2. 根据openid查询用户表，判断用户是否注册
    *   1. 如果没有注册，则自动创建新用户，并保存到用户表
    *   2. 如果已经注册，则直接获取用户信息： id
    *  3. 返回User对象
    * */
    User wxLogin(UserLoginDTO userLoginDTO);
}
