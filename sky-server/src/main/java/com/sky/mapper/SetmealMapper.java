package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {
    /*
    * 判断套餐是否关联了分类
    * @param categoryId
    * @return Integer (关联数量)
    * */
    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
