package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /*
    * 批量插入菜品口味数据
    * @param List<DishFlavors> flavors
    * @return
    * 注意：
    *  1. 使用动态SQL批量插入，
    *  2. 由于需要处理dish_id字段，但是这个字段前端无法传递回，因为此时dish还没插入，dish_id还没生成
    *  3. 需要在DishMapper中的insert方法中使用id值自动回填（MyBatis的useGeneratedKeys属性），将id获取到
    *  4. 然后service层代码中，将Dish的id设置到每个DishFlavor对象的dishId字段中
    * */
    void insertBatch(List<DishFlavor> flavors);

    /*
    * 批量删除菜品口味数据
    * @param List<Long> ids (dish_id)
    * @return
    * */
    void deleteByDishIds(List<Long> ids);

    /*
    * 根据dishId查询对应的口味数据，用于修改菜品时回显数据
    * @param Long id (dish_id)
    * @Return List<DishFlavor>
    * 注意：
    *  1. dish_id不是dish_flavor表的主键id
    *  2. dish_id是dish_flavor表的外键
    *  3. dish_id对应多个口味数据，所以返回List<DishFlavor
    * */
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{id}")
    List<DishFlavor> selectByDishId(Long id);
}
