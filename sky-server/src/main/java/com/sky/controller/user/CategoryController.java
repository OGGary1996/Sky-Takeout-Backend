package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name = "User Category Controller", description = "User Category Controller")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "Query Categories", description = "Query Categories by Type")
    public Result<List<Category>> list(Integer type) {
        log.info("Query Categories by Type: {}", type);
        List<Category> list = categoryService.selectList(type);
        return Result.success(list);
    }
}
