package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Tag(name = "Workspace Controller", description = "Workspace related endpoints")
@Slf4j
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /*
    * 获取今日数据，包括营业额、有效订单数、订单完成率、平均客单价、新增用户数
    * @return BusinessDataVO
    * */
    @GetMapping("/businessData")
    @Operation(summary = "Get today's business data")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("Fetching today's business data");
        // 获取今日的开始时间与结束时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO todayData = workspaceService.getTodayData(begin, end);
        return Result.success(todayData);
    }

    /*
    * 获取订单管理数据，包括各个状态的订单数量：待接单、待派送、已完成、已取消、全部订单
    * @return OrderOverViewVO
    * */
    @GetMapping("/overviewOrders")
    @Operation(summary = "Get order overview data")
    public Result<OrderOverViewVO> getOrderOverView() {
        log.info("Fetching order overview data");
        // 获取今日的开始时间与结束时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        OrderOverViewVO orderOverView = workspaceService.getOrderOverView(begin, end);
        return Result.success(orderOverView);
    }

    /*
    * 获取菜品总览数据，包括已启售和已停售的菜品数量
    * @return DishOverViewVO
    * */
    @GetMapping("/overviewDishes")
    @Operation(summary = "Get dish overview data")
    public Result<DishOverViewVO> getDishOverView() {
        log.info("Fetching dish overview data");
        DishOverViewVO dishOverView = workspaceService.getDishOverView();
        return Result.success(dishOverView);
    }

    /*
    * 获取套餐总览数据，包括已启售数量和已停售数量
    * @return SetmealOverViewVO
    * */
    @GetMapping("/overviewSetmeals")
    @Operation(summary = "Get setmeal overview data")
    public Result<SetmealOverViewVO> getSetmealOverView() {
        log.info("Fetching setmeal overview data");
        SetmealOverViewVO setmealOverView = workspaceService.getSetmealOverView();
        return Result.success(setmealOverView);
    }
}
