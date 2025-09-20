package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /*
    * 新增分类
    * @param categoryDTO
    * @return
    * */
    void insertCategory(CategoryDTO categoryDTO);

    /*
    * 分页查询分类
    * @param categoryPageQueryDTO
    * @return PageResult
    * */
    PageResult selectPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /*
    * 根据ID删除分类
    * @param id
    * @return
    * */
    void deleteById(Long id);

    /*
    * 根据ID查询分类
    * 用于修改分类时回显数据
    * @param id
    * @return Category
    * */
    Category selectById(Long id);

    /*
    * 根据ID修改分类
    * @param categoryDTO
    * @return
    * */
    void updateCategoryInfo(CategoryDTO categoryDTO);

    /*
    * 根据ID修改分类状态
    * @param status, id
    * @return
    * */
    void updateCategoryStatus(Integer status, Long id);

    /*
    * 根据类型查询分类
    * 可以传type参数，也可以不传，不传则查询所有status为1的分类
    * @param type
    * @return List<Category>
    * */
    List<Category> selectList(Integer type);
}
