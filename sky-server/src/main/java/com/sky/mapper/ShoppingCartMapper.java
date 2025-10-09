package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /*
    * 根据用户id和菜品id/套餐id查询购物车中是否存在该菜品/套餐
    * @param ShoppingCart
    * @return List<ShoppingCart>
    * 注意：此处虽然返回了List，但是实际上只有一个数据，因为一种菜品/套餐在购物车中只能存在一个，通过数量来区分
    * */
    List<ShoppingCart> getByUserIdAndDishOrSetmealId(ShoppingCart shoppingCart);

    /*
    * 如果存在，则数量 + 1
    * @param ShoppingCart
    * @return
    * */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /*
    * 如果不存在，并且是Dish，则添加到购物车，数量默认为1，冗余字段查询dish相关的表
    * @param ShoppingCart
    * @return
    * */
    @Insert("INSERT INTO shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time)" +
            " VALUES (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insertIntoShoppingCart(ShoppingCart shoppingCart);

    /*
    * 查看购物车所有商品
    * @param Long userId
    * @return List<ShoppingCart>
    * */
    @Select("SELECT id, name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time" +
            " FROM shopping_cart WHERE user_id = #{userId} ORDER BY create_time")
    List<ShoppingCart> selectListByUserId(Long userId);

    /*
    * 清空购物车
    * @param Long userId
    * @return
    * */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
