package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Setmeal Management")
@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    private final SetmealService setmealService;
    @Autowired
    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    /*
    * 新增套餐
    * @param SetmealDTO
    * @return
    * */
    @Operation(summary = "Add Setmeal" , description = "Add a new setmeal with its details")
    @PostMapping
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("SetmealDTO: {}", setmealDTO);
        setmealService.insertSetmealWithDish(setmealDTO);
        return Result.success();
    }

    /*
    * 条件分页查询
    * @param SetmealPageQueryDTO
    * @return Result<PageResult>
    * */
    @Operation(summary = "Setmeal Pagination Query" , description = "Query setmeals with pagination and optional filters")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("SetmealPageQueryDTO: {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.selectPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 单个/批量删除套餐
    * @param List<Long> ids
    * @return Result<String>
    * */
    @Operation(summary = "Delete Setmeals" , description = "Delete one or more setmeals by their IDs")
    @DeleteMapping
    public Result<String> deleteBatch(@RequestParam List<Long> ids){
        log.info("Setmeal IDs to delete: {}", ids);
        setmealService.deleteByIds(ids);
        return Result.success();
    }

    /*
    * 根据id查询套餐信息，用于回显套餐信息
    * @param Long id
    * @return Result<SetmealVO>
    * */
    @Operation(summary = "Get Setmeal by ID" , description = "Retrieve setmeal details by its ID for display")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("Setmeal ID to retrieve: {}", id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }
    /*
    * 根据id修改套餐
    * @param SetmealDTO
    * @return Result<String>
    * */
    @Operation(summary = "Update Setmeal" , description = "Update an existing setmeal with new details")
    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("SetmealDTO to update: {}", setmealDTO);
        setmealService.updateSetmealWithDish(setmealDTO);
        return Result.success();
    }

    /*
    * 启售停售套餐
    * @param Integer status, Long id
    * @return Result<String>
    * */
    @Operation(summary = "Change Setmeal Status" , description = "Enable or disable a setmeal by its ID")
    @PostMapping("/status/{status}")
    public Result<String> setmealStartStop(@PathVariable Integer status, @RequestParam Long id){
        log.info("Setmeal ID: {}, Status to set: {}", id, status);
        setmealService.setSetmealStatus(status, id);
        return Result.success();
    }
}
