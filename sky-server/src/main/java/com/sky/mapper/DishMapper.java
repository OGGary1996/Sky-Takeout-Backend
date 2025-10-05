package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /*
    * 判断菜品是否关联了分类，如果有关联，则不能删除该分类
    * @param categoryId
    * @return Integer (关联数量)
    * */
    @Select("SELECT COUNT(*) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*
    * 新增菜品基本信息
    * @param dish
    * @return
    * 注意：
    *  1. 新增菜品时，对于categoryId，前端显示为下拉框categoryName，并不是显示categoryId
    *  2. 需要额外的接口向前端传递categoryId与categoryName的对应关系
    *  3. 接口已在CategoryController中实现
    * */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /*
    * 条件分页查询 + 多表查询
    * @param DishPageQueryDTO dishPageQueryDTO
    * @Return Page<DishVO>
    * 注意：
    *  1. 显示菜品信息时，对于category分类的信息，前端直接显示为表格categoryName，并非通过下拉框显示categoryName，
    *  2. 所以与新增菜品接口不同，不需要额外的接口向前端传递categoryId与categoryName的对应关系，多表查询直接显示即可
    * */
    Page<DishVO> selectPage(DishPageQueryDTO queryDTO);


    /*
    * 查询ids中的菜品status=1的数量，如果数量大于0，说明有关联，不能删除
    * @param ids
    * @return Integer (status=1的数量)
    * */
    Integer countByIdsAndStatus(List<Long> ids);
    /*
    * 根据ids获取到image字段，用于删除图片
    * @param List<Long> ids
    * @return List<String> (image字段)
    * */
    List<String> getImagesByIds(List<Long> ids);
    /*
    * 批量/单个删除菜品
    * @param List<Long> ids
    * @return
    * */
    void deleteBatch(List<Long> ids);


    /*
    * 根据id查询dish，用于修改时回显数据
    * @param Long id
    * @Return Dish
    * */
    @Select("SELECT id, name, category_id, price, image, description, status, update_time FROM dish WHERE id = #{id}")
    Dish selectById(Long id);
    /*
    * 修改菜品基本信息
    * @param Dish dish
    * @Return
    * 注意：
    *  1. 新增菜品时，对于categoryId，前端显示为下拉框categoryName，并不是显示categoryId
    *  2. 需要额外的接口向前端传递categoryId与categoryName的对应关系
    *  3. 接口已在CategoryController中实现
    * */
    @AutoFill(OperationType.UPDATE)
    void updateById(Dish dish);

    /*
    * 获取所有在售的dish
    * @param Long categoryId，非必须，可以根据分类id查询
    * @Return List<DishVO>
    * */
    List<Dish> listByCategoryId(Long categoryId);

    /*
    * 根据ids判断是否有停售的菜品，如果有停售的菜品，则不能启售响应的套餐
    * @param List<Long> ids
    * @Return Integer (status=0的数量)
    * */
    Integer countByIdsAndNotStatus(List<Long> ids);
}
