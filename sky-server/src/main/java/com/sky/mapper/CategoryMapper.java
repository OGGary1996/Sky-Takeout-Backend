package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /*
    * 新增分类
    * @param category
    * @return
    * */
    @AutoFill(OperationType.INSERT)
    @Insert("INSERT INTO category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertCategory(Category category);

    /*
    * 分页查询分类
    * @param categoryPageQueryDTO
    * @return Page<Category>
    * */
    Page<Category> selectPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /*
    * 删除分类
    * @param id
    * @return
    * */
    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Long id);

    /*
    * 根据ID查询分类
    * 用于修改分类时回显数据
    * @param id
    * @return Category
    * */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Long id);
    /*
    * 根据ID修改分类
    * @param category
    * @return
    * 注意：这个方法是通用修改方法，使用动态SQL，可以修改任意字段
    * */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /*
    * 根据类型查询分类
    * 可以传type参数，也可以不传，不传则查询所有status为1的分类
    * @param type
    * @return List<Category>
    * */
    List<Category> selectList(Integer type);
}
