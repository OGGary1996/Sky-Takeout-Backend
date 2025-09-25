package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /*
    * 新增菜品
    * @param DishDto dishDto
    * @return
    * */
    void insertWithFlavor(DishDTO dishDTO);

    /*
    * 菜品条件分类查询
    * @param DishPageQueryDTO dishPageQueryDTO
    * @Return Result<PageResult>
    * */
    PageResult selectPage(DishPageQueryDTO dishPageQueryDTO);

    /*
    * 批量/单个删除菜品
    * @param Long[] ids
    * @Return Result<String>
    * */
    void deleteBatch(List<Long> ids);

    /*
    * 根据id查询dish + dish_flavor,用于修改菜品时回显数据
    * @param Long id
    * @Return DishVO
    * 注意：
    *  1. 需要同时操作两张表：dish,dish_flavor
    * */
    DishVO selectByIdWithFlavor(Long id);

    /*
    * 根据id修改菜品
    * @param DishDTO dishDTO
    * @Return
    * 注意：
    *  1. 需要同时操作两张表：dish,dish_flavor
    *  2. 对于dish_flavor表，先删除原有口味数据，再插入口味数据，保证数据一致性
    * */
    void updateWithFlavor(DishDTO dishDTO);

    /*
    * 根据id修改菜品启售状态
    * @param Integer status, Long id
    * @return
    * */
    void updateStatusById(Integer status, Long id);

    /*
    * 显示所有在售的dish
    * @param Long categoryId，非必须，可以根据分类id查询
    * @Return List<DishVO>
    * */
    List<DishVO> listByCategoryId(Long categoryId);
}
