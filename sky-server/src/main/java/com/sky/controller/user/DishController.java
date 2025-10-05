package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Tag(name = "User Dish Controller", description = "User Dish Controller")
@Slf4j
public class DishController {
    private final DishService dishService;
    @Autowired
    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * 根据分类id查询菜品
     *
     * @param  categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "Query Dishes by Category ID", description = "Query Dishes by Category ID")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("Get Dishes by Category ID: {}", categoryId);
        List<DishVO> list = dishService.listByCategoryId(categoryId);
        return Result.success(list);
    }
}
