package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /*
    * 查询ids中的菜品关联了setmeal，如果数量大于0，说明有关联，不能删除
    * @param ids
    * @return Integer (关联数量)
    * */
    Integer countByDishIds(List<Long> ids);

    /*
    * 根据dishId,获取到setmeal_id字段，用于修改dish停售时，同时获取到需要停售的setmeal_id
    * @param Long id(dish_id)
    * @return List<Long> (setmeal_id字段)
    * */
    @Select("SELECT setmeal_id FROM setmeal_dish WHERE dish_id = #{id}")
    List<Long> getSetmealIdsByDishId(Long id);

    /*
    * 批量新增套餐和菜品的关联数据
    * @param List<SetmealDish> setmealDishes
    * @return
    * */
    void insertBatch(List<SetmealDish> setmealDishes);

    /*
    * 根据ids(setmeal_id)批量删除套餐和菜品的关联数据
    * @param List<Long> ids(setmeal_id)
    * @return
    * */
    void deleteBySetmealIds(List<Long> ids);

    /*
    * 根据id(setmeal_id)查询套餐和菜品的关联数据,用于回显
    * @param Long id(setmeal_id)
    * @return List<SetmealDish>
    * */
    @Select("SELECT id, setmeal_id, dish_id, name, price, copies FROM setmeal_dish WHERE setmeal_id = #{id}")
    List<SetmealDish> selectBySetmealId(Long id);

    /*
    * 根据setmealId查询对应的菜品
    * @param Long setmeal_id
    * @return List<DishItemVO>
    * 注意：
    *  1. 需要联表查询，setmeal_dish和dish
    * */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

}
