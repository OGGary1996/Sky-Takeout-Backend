package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkspaceMapper {
    /*
    * 获取菜品的总览信息。包括已启售数量和已停售数量
    * @param status 菜品状态
    * @return Integer 已启售菜品数量和已停售菜品数量
    * */
    @Select("SELECT COUNT(*) FROM dish WHERE status = #{status}")
    Integer getDishCountByStatus(Integer status);

    /*
    * 获取菜品的总览信息。包括已启售数量和已停售数量
    * @param status 菜品状态
    * @return Integer 已启售菜品数量和已停售菜品数量
    * */
    @Select("SELECT COUNT(*) FROM setmeal WHERE status = #{status}")
    Integer getSetmealCountByStatus(Integer status);

}
