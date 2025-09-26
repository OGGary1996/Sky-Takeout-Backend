package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
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
    * 新增套餐
    * @param setmeal
    * @return
    * 注意：
    *  1. 新增setmeal_dish表时，需要用到setmeal_id字段，这个字段对应setmeal表的主键id
    *  2. 所以需要使用MyBatis的generatedKeys获取自增主键id
    * */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /*
    * 条件分页查询
    * @param SetmealPageQueryDTO: name（模糊搜索）,categoryId,status
    * @return List<Setmeal>
    * 注意：
    *  1.显示套餐信息时，其中的categoryName是关联category表的name字段，前端直接显示categoryName，并非通过下拉框显示
    *  2.所以与新增套餐接口不同，不需要额外的接口向前端传递categoryId与categoryName的对应关系，多表查询直接显示即可
    * */
    Page<SetmealVO> selectPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /*
    * 判断是否在售
    * @param List<Long> ids
    * @return Integer (在售数量)
    * */
    Integer countByIdsAndStatus(List<Long> ids);
    /*
    * 获取到images字段，用于删除阿里云OSS中的图片
    * @param List<Long> ids
    * @return List<String> (images字段)
    * */
    List<String> getImagesByIds(List<Long> ids);
    /*
    * 批量删除套餐
    * @param List<Long> ids
    * @return
    * */
    void deleteByIds(List<Long> ids);

    /*
    * 根据id查询套餐信息
    * @param Long id
    * @return Setmeal
    * */
    @Select("SELECT id, category_id, name, price, status, description, image, update_time FROM setmeal WHERE id = #{id}")
    Setmeal selectById(Long id);
    /*
    * 通用修改方法
    * @param setmeal
    * @return
    * */
    @AutoFill(OperationType.UPDATE)
    void updateById(Setmeal setmeal);
}
