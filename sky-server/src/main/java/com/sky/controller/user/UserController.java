package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Tag(name = "User", description = "User API")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtProperties jwtProperties;
    @Autowired
    public UserController(UserService userService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
    }

    /*
    * 微信登录接口
    * @pram UserLoginDTO（code） 前端传过来的临时登录凭证code
    * @return Result<UserLoginVO> (id,openid,token) 返回用户信息和token
    * 流程；
    *  1. 接收参数，调用Service层
    *  2. 接收Service返回的User对象
    *  3. 调用JWT工具类，生成token
    *  4. 将用户信息和token封装到UserLoginVO中，并返回
    * */
    @RequestMapping("/login")
    @Operation( summary = "WeChat login", description = "WeChat login interface" )
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        // 1. 接收参数，调用Service层
        log.info("User login, userLoginDTO: {}", userLoginDTO);
        User user = userService.wxLogin(userLoginDTO);
        // 2. 调用JWT工具类，生成token
          // 2.1 准备claims，将用户的id作为payload，注意：不能把敏感信息放到payload中，比如openid
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
          // 2.2 调用JwtUtil.createJWT()生成token
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        // 3. 将用户信息和token封装到UserLoginVO中，并返回
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();
        return Result.success(userLoginVO);
    }
}
