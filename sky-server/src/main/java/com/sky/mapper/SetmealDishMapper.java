package com.sky.mapper;

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
}
