package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Tag(name = "User Setmeal Controller", description = "User Setmeal Controller")
public class SetmealController {
    private final SetmealService setmealService;
    @Autowired
    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    /**
     * 根据categoryId查询所有在售套餐
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "Query Setmeals by Category ID", description = "Query Setmeals by Category ID")
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("SetmealController.list: {}", categoryId);
        List<Setmeal> setmeals = setmealService.selectList(categoryId);
        return Result.success(setmeals);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param setmealId
     * @return
     */
    @GetMapping("/dish/{id}")
    @Operation(summary = "Query Dishes by Setmeal ID", description = "Query Dishes by Setmeal ID")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long setmealId) {
        List<DishItemVO> list = setmealService.getDishItemById(setmealId);
        return Result.success(list);
    }
}
