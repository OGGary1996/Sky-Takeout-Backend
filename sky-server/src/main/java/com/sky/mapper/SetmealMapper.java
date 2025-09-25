package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /*
    * 判断套餐是否关联了分类
    * @param categoryId
    * @return Integer (关联数量)
    * */
    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*
    * 批量停售套餐，用于停售dish时，同时停售关联的套餐
    * @param List<Long> setmealIds
    * @return
    * */
    @AutoFill(OperationType.UPDATE)
    void setmealStopBatch(List<Long> setmealIds);

    /*
    * 通用修改方法
    * @param setmeal
    * @return
    * */
    @AutoFill(OperationType.UPDATE)
    void updateById(Setmeal setmeal);
}
