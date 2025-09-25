package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DishController", description = "Dish Management")
@RestController
@RequestMapping("admin/dish")
@Slf4j
public class DishController {
    private final DishService dishService;
    @Autowired
    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /*
    * 新增菜品
    * @param DishDto dishDto
    * @Return Result<String>
    * */
    @Operation(summary = "Add new dish", description = "Add new dish" )
    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("dishDTO:{}", dishDTO);
        dishService.insertWithFlavor(dishDTO);
        return Result.success();
    }

    /*
    * 菜品条件分类查询
    * @param DishPageQueryDTO dishPageQueryDTO
    * @Return Result<PageResult>
    * */
    @Operation(summary = "Get dish by condition", description = "Get dish by condition" )
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("dishPageQueryDTO:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.selectPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 批量/单个删除菜品
    * @param Long[] ids
    * @Return Result<String>
    * */
    @Operation(summary = "Delete dish by ids", description = "Delete dish by ids" )
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /*
    * 根据Id查询dish + dish_flavor,用于修改菜品时回显数据
    * @param Long id
    * @Return Result<DishVO>
    * */
    @Operation(summary = "Get dish by id", description = "Get dish by id" )
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("id:{}", id);
        DishVO dishVO = dishService.selectByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /*
    * 根据id修改菜品
    * @param DishDTO dishDTO
    * @Return Result<String>
    * */
    @Operation(summary = "Update dish by id", description = "Update dish by id" )
    @PutMapping
    public Result<String> updateById(@RequestBody DishDTO dishDTO){
        log.info("dishDTO:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /*
    * 菜品起售/停售
    * @param @PathVariable Integer status, @RequestParam Long id
    * @Return Result<String>
    * */
    @Operation(summary = "Change dish status", description = "Change dish status" )
    @PostMapping("/status/{status}")
    public Result<String> dishStartStop(@PathVariable Integer status, @RequestParam Long id){
        log.info("status:{}, id:{}", status, id);
        dishService.updateStatusById(status, id);
        return Result.success();
    }

    /*
    * 获取所有在售的dish
    * @param Long categoryId,非必须，可以根据分类id查询
    * @Return Result<List<DishVO>>
    * */
    @Operation(summary = "Get all on-sale dish", description = "Get all on-sale dish" )
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam(required = false) Long categoryId){
        log.info("categoryId:{}", categoryId);
        List<DishVO> dishVOList = dishService.listByCategoryId(categoryId);
        return Result.success(dishVOList);
    }
}
