package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /*
    * 新增dish/setmeal到购物车
    * @param shoppingCartDTO
    * @return
    * 注意：
    *  1. 首先需要判断购物车中是否存在该菜品/套餐
    *   1.1 如果存在，则数量 + 1
    *   1.2 如果不存在，则添加到购物车，数量默认为1
    *  2. 判断是否为dish还是setmeal
    *  3. 如果是dish，还需要判断口味
    *  4. 需要设置用户id和时间
    * */
    void addToCart(ShoppingCartDTO shoppingCartDTO);

    /*
    * 查看购物车所有商品
    * @param 空，不需要传递参数，每次请求都被拦截器
    * 设置了用户id到TheadLocal
    * @return List<ShoppingCart>
    * */
    List<ShoppingCart> selectList();

    /*
    * 清空购物车
    * @param 空，不需要传递参数，每次请求都被拦截器设置了用户id到TheadLocal
    * @return
    * */
    void cleanCart();
}
