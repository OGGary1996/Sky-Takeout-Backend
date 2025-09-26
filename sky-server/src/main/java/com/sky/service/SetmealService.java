package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /*
    * 新增套餐
    * @param SetmealDTO
    * @return
    * 注意：
    *  1. 前端需要显示categoryId和categoryName对应的下拉框，接口已经实现
    *  2. 前端需要显示dishId 和dishName对应的下拉框，接口已经实现
    *  3. 前端需要实现图片上传，接口已经实现
    *  4. 继续实现新增套餐功能
    * */
    void insertSetmealWithDish(SetmealDTO setmealDTO);

    /*
    * 条件分页查询
    * @param SetmealPageQueryDTO
    * @return PageResult
    * */
    PageResult selectPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /*
    * 单个/批量删除套餐
    * @param List<Long> ids
    * @return
    * 注意：
    *  1. 如果status = 1 ，则不能删除
    *  2. 删除套餐之后，需要同时阐述setmeal_dish表中的数据
    *  3. 删除套餐后，需要同时删除阿里云OSS中的图片
    * */
    void deleteByIds(List<Long> ids);

    /*
    * 根据id查询套餐信息和对应的菜品信息
    * @param Long id
    * @return SetmealVO
    * 注意：
    *  1. 需要查询套餐表(setmeal)和套餐菜品关系表(setmeal_dish)
    * */
    SetmealVO selectById(Long id);

    /*
    * 根据id修改套餐信息和对应的菜品信息
    * @param SetmealDTO
    * @return
    * 注意：
    *  1. 需要修改套餐表(setmeal)和套餐菜品关系
    * */
    void updateSetmealWithDish(SetmealDTO setmealDTO);

    /*
    * 停售/起售套餐
    * @param Integer status, Long id
    * @return
    * 注意：
    *  1. 停售套餐与dish是否启用无关，停售dish时，如果关联了套餐，则套餐会自动停售
    *  2. 启售套餐时，如果其中dish有停售的，则不能启售
    * */
    void setSetmealStatus(Integer status ,Long id);

}
