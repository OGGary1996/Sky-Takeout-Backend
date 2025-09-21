package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper; // 注入 CategoryMapper
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
        this.categoryMapper = categoryMapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    /*
    * 新增分类
    * @param categoryDTO
    * @return
    * */
    @Override
    public void insertCategory(CategoryDTO categoryDTO) {
        // 1. 将 CategoryDTO 转换为 Category 实体
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // 2. 设置其他必要的字段，例如创建时间、更新时间等
        // 已经设置了AOP自动填充
        category.setStatus(StatusConstant.DISABLE); // 默认状态为禁用
        // 3. 调用 Mapper 层的方法将数据插入数据库
        categoryMapper.insertCategory(category);
    }

    /*
    * 分页查询分类
    * @param categoryPageQueryDTO
    * @return PageResult
    * */
    @Override
    public PageResult selectPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 1. 首先开启分页
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        // 2. 构建查询条件并执行查询
        Page<Category> page = categoryMapper.selectPage(categoryPageQueryDTO);
        // 3. 封装并返回结果
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }

    /*
    * 根据ID删除分类
    * @param id
    * @return
    * */
    @Override
    public void deleteById(Long id) {
        // 1. 判断是否关联菜品或套餐
        if (dishMapper.countByCategoryId(id) != 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }else if (setmealMapper.countByCategoryId(id) != 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 2. 如果没有关联，则执行删除操作
        categoryMapper.deleteById(id);
    }

    /*
    * 根据ID查询分类
    * 用于修改分类时回显数据
    * @param id
    * @return Category
    * */
    @Override
    public Category selectById(Long id) {
        return categoryMapper.selectById(id);
    }
    /*
    * 根据ID修改分类
    * @param categoryDTO
    * @return
    * */
    @Override
    public void updateCategoryInfo(CategoryDTO categoryDTO) {
        // 1. 将 CategoryDTO 转换为 Category 实体
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // 2. 设置其他必要的字段，例如更新时间等
        //  已经设置了AOP自动填充
        // 3. 调用 Mapper 层的方法将数据更新到数据库
        // 注意：这里的update方法是通用修改的方法，通过动态SQL实现
        categoryMapper.update(category);
    }

    /*
    * 根据ID修改分类状态
    * @param status, id
    * @return
    * */
    @Override
    public void updateCategoryStatus(Integer status, Long id) {
        // 1. 创建 Category 对象并设置 ID 和状态
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        // 已经设置了AOP自动填充
        // 2. 调用 Mapper 层的方法将数据更新到数据库
        categoryMapper.update(category);
    }

    /*
    * 根据类型查询分类
    * 可以传type参数，也可以不传，不传则查询所有status为1的分类
    * @param type
    * @return List<Category>
    * */
    @Override
    public List<Category> selectList(Integer type) {
        return categoryMapper.selectList(type);
    }
}
