package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 菜品&套餐分类管理
* */
@Tag(name = "CategoryController", description = "Dish and Set Meal Category Management")
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    /*
    * 新增分类
    * @param categoryDTO
    * @return
    * */
    @Operation(summary = "Add Category", description = "Add a new dish or set meal category")
    @PostMapping
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("Add category:{}" , categoryDTO);
        categoryService.insertCategory(categoryDTO);
        return Result.success();
    }

    /*
    * 分类的分页查询
    * @param CategoryPageQueryDTO
    * 注意：这里通过RequestParam传递参数，Spring自动封装到DTO对象中，不能使用@RequestBody
    * @return Result<PageResult>
    * */
    @Operation(summary = "Category Pagination Query", description = "Paginated query of categories")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("Category pagination query:{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.selectPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 根据ID删除分类
    * @param id
    * 注意：这里通过@RequestParam传递参数，不使用@PathVariable
    * @return Result<String>
    * */
    @Operation(summary = "Delete Category by ID", description = "Delete a category by its ID")
    @DeleteMapping
    public Result<String> deleteCategory(@RequestParam Long id){
        log.info("Delete category by id:{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /*
    * 根据ID显示分类信息
    * 用于修改分类时回显数据
    * @param id
    * @return Result<Category>
    * */
    @Operation(summary = "Get Category by ID", description = "Retrieve category information by its ID for editing")
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id){
        log.info("Get category by id:{}", id);
        Category category = categoryService.selectById(id);
        return Result.success(category);
    }
    /*
    * 根据ID修改分类
    * @param categoryDTO
    * @return Result<String>
    * */
    @Operation(summary = "Update Category by ID", description = "Update category information by its ID")
    @PutMapping
    public Result<String> updateCategoryInfo(@RequestBody CategoryDTO categoryDTO){
        log.info("Update category:{}", categoryDTO);
        categoryService.updateCategoryInfo(categoryDTO);
        return Result.success();
    }

    /*
    * 根据ID修改分类status
    * @param status + id (路径参数 + 查询参数)
    * @return Result<String>
    * */
    @Operation(summary = "Update Category Status by ID", description = "Update the status of a category by its ID")
    @PostMapping("/status/{status}")
    public Result<String> updateCategoryStatus(@PathVariable Integer status , @RequestParam Long id){
        log.info("Update category status: id={}, status={}", id, status);
        categoryService.updateCategoryStatus(status,id);
        return Result.success();
    }

    /*
    * 根据type查询启用的分类
    * 注意：type非必须，如果不传参，则表示查询所有启用状态下的分类
    * 不需要考虑分页
    * @param type
    * @return Result<List<Category>>
    * */
    @Operation(summary = "List Categories by Type", description = "List all active categories, optionally filtered by type")
    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type){
        log.info("List categories by type:{}", type);
        List<Category> categoryList = categoryService.selectList(type);
        return Result.success(categoryList);
    }
}
