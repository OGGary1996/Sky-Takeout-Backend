package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    /*
    * 新增dish/setmeal到购物车
    * @param shoppingCartDTO
    * @return
    * 注意：
    *  1. 首先需要判断购物车中是否存在该菜品/套餐
    *   1.1 如果存在，则数量 + 1
    *   1.2 如果不存在，继续判断：
    *  2. 判断是否为dish还是setmeal
    *  3. 分别调用dishMapper/setmealMapper补充冗余字段，避免多表查询
    * */
    @Transactional
    @Override
    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.debug("Current User ID: {}", shoppingCart.getUserId());

        // 1. 判断dish/setmeal是否已经存在
        List<ShoppingCart> dishOrSetmeal = shoppingCartMapper.getByUserIdAndDishOrSetmealId(shoppingCart);
        log.debug("Existing items in cart: {}", dishOrSetmeal);
        if (!dishOrSetmeal.isEmpty() && dishOrSetmeal != null){ // 1.1 如果存在，则直接+ 1
            // 注意：此处虽然返回了List，但是实际上只有一个数据，因为一种菜品/套餐在购物车中只能存在一个，通过数量来区分
            // 获取到购物车中的第一个数据
            ShoppingCart cartItem = dishOrSetmeal.get(0);
            cartItem.setNumber(cartItem.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cartItem);
        }else{ // 1.2 如果不存在，则添加新的数据到购物车
            // 注意，此时shoppingCart中已经包含了用户id,但是不包含冗余字段，需要多表查询
            // 2.判断是否为dish还是setmeal,才能判断冗余数据多表查询哪个表
            if(shoppingCart.getDishId() != null){ // 是dish,调用dishMapper补充冗余字段，避免多表查询
                Dish dish = dishMapper.selectById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{ // 是setmeal,调用setmealMapper补充冗余字段，避免多表查询
                Setmeal setmeal = setmealMapper.selectById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1); // 新增到购物车，数量默认为1
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 将设置好的shoppingCart插入到购物车表
            shoppingCartMapper.insertIntoShoppingCart(shoppingCart);
        }

    }

    /*
    * 查看购物车所有商品
    * @param 空，不需要传递参数，每次请求都被拦截器
    * 设置了用户id到TheadLocal
    * @return List<ShoppingCart>
    * */
    @Override
    public List<ShoppingCart> selectList() {
        Long currentId = BaseContext.getCurrentId();
        log.debug("Current User ID: {}", currentId);
        return shoppingCartMapper.selectListByUserId(currentId);
    }

    /*
    * 清空购物车
    * @param 空，不需要传递参数，每次请求都被拦截器设置了用户id到TheadLocal
    * @return
    * */
    @Override
    public void cleanCart() {
        Long currentId = BaseContext.getCurrentId();
        log.debug("Current User ID: {}", currentId);
        shoppingCartMapper.deleteByUserId(currentId);
    }
}
