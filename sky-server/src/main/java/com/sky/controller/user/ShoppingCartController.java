package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "Shopping Cart Controller", description = "Endpoints for managing the shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /*
    * 添加商品到购物车，添加dish（可能存在口味选择）/setmeal
    * @param ShoppingCartDTO
    * @return
    * */
    @PostMapping("/add")
    @Operation(summary = "Add item to shopping cart", description = "Add a dish or setmeal to the shopping cart")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("Adding item to shopping cart: {}", shoppingCartDTO);
        shoppingCartService.addToCart(shoppingCartDTO);
        return Result.success();
    }

    /*
    * 查看购物车所有商品
    * @param 空，不需要传递参数，每次请求都被拦截器设置了用户id到TheadLocal
    * @return List<ShoppingCart>
    * */
    @GetMapping("/list")
    @Operation(summary = "List all items in shopping cart", description = "Retrieve all items currently in the shopping cart")
    public Result<List<ShoppingCart>> list(){
        log.info("List all items in shopping cart");
        List<ShoppingCart> shoppingCartList = shoppingCartService.selectList();
        return Result.success(shoppingCartList);
    }

    /*
    * 清空购物车
    * @param 空，不需要传递参数，每次请求都被拦截器设置了用户id到TheadLocal
    * @return
    * */
    @DeleteMapping("/clean")
    @Operation(summary = "Clear shopping cart", description = "Remove all items from the shopping cart")
    public Result<String> clean(){
        log.info("Clearing shopping cart");
        shoppingCartService.cleanCart();
        return Result.success();
    }
}
