package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /*
    * 根据openid查询用户
    * 用于判断用户是否注册
    * @param openid 微信用户唯一标识
    * @return User对象
    * */
    @Select("SELECT id,openid,name,phone,sex,id_number,avatar,create_time FROM user WHERE openid = #{openid}")
    User getByOpenid(String openid);

    /*
    * 新增用户
    * @param user 用户对象
    * @return
    * 注意：
    *  1. 如果没有注册，则进行注册，保存用户信息到用户表
    *  2. 并且，需要主键回填，因为controller中需要使用到user的id
    * */
    void insert(User user);
}
