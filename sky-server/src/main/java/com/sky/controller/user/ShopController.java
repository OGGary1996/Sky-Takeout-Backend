package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

// 由于管理端存在同样的 ShopController，所以这里需要修改Bean名称
@RestController("userShopController")
@RequestMapping("/user/shop")
@Tag(name = "Shop Management")
@Slf4j
public class ShopController {
    private static final String KEY = "sky:SHOP_STATUS";
    //注入StringRedisTemplate
    private final StringRedisTemplate stringRedisTemplate;
    @Autowired
    public ShopController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /*
    * 查询店铺营业情况
    * @param
    * @return Result<Integer>
    * */
    @Operation(summary = "Query Shop Status" , description = "Query the current operational status of the shop")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("Querying shop status");
        //从Redis中获取店铺营业状态
        // 需要判断是否为空，为空则表示店铺关闭
        String statusStr = stringRedisTemplate.opsForValue().get(KEY);
        if(statusStr == null){
            log.info("Status is null, shop is currently closed");
            return Result.success(StatusConstant.DISABLE);
        }
        Integer status = Integer.valueOf(statusStr);
        log.info("Current shop status: {}", status == 1 ? "Open" : "Closed");
        return Result.success(status);
    }
}
