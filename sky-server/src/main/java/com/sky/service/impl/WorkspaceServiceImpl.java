package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private final ReportMapper reportMapper;
    private final WorkspaceMapper workspaceMapper;
    @Autowired
    public WorkspaceServiceImpl(ReportMapper reportMapper, WorkspaceMapper workspaceMapper) {
        this.reportMapper = reportMapper;
        this.workspaceMapper = workspaceMapper;
    }

    /*
    * 获取今日数据，包括营业额、有效订单数、订单完成率、平均客单价、新增用户数
    * mapper的具体实现已经在reportMapper中完成
    * @return BusinessDataVO
    * */
    @Override
    public BusinessDataVO getTodayData(LocalDateTime begin, LocalDateTime end) {
        // 准备参数
        Map<String,Object> params = new HashMap<>();
        params.put("begin", begin);
        params.put("end", end);
        params.put("status", Orders.COMPLETED);

        // 1. 获取当天营业额
        Double turnoverByDate = reportMapper.getTurnoverByDate(params);
        if (turnoverByDate == null) {
            turnoverByDate = 0.0;
        }
        // 2. 获取有效订单数
        Integer validOrderCountByDate = reportMapper.getOrderCountByDate(params);
        if (validOrderCountByDate == null) {
            validOrderCountByDate = 0;
        }
        // 3. 获取总订单数
        params.remove("status");
        Integer totalOrderCountByDate = reportMapper.getOrderCountByDate(params);
        if (totalOrderCountByDate == null) {
            totalOrderCountByDate = 0;
        }
        // 4. 获取订单完成率
        Double orderCompletionRate;
        if (totalOrderCountByDate == 0) {
            orderCompletionRate = 0.0;
        }else {
            orderCompletionRate = (double) validOrderCountByDate / totalOrderCountByDate;
        }
        // 5. 获取平均客单价
        Double averageCustomerPrice;
        if (validOrderCountByDate == 0) {
            averageCustomerPrice = 0.0;
        }else {
            averageCustomerPrice = turnoverByDate / validOrderCountByDate;
        }
        // 6. 获取新增用户数
        Integer userByDate = reportMapper.getUserByDate(params);

        // 封装结果并返回
        return BusinessDataVO.builder()
                .turnover(turnoverByDate)
                .validOrderCount(validOrderCountByDate)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(averageCustomerPrice)
                .newUsers(userByDate)
                .build();
    }

    /*
    * 获取订单管理数据，包括各个状态的订单数量：待接单、待派送、已完成、已取消、全部订单
    * @return OrderOverViewVO
    * */
    @Override
    public OrderOverViewVO getOrderOverView(LocalDateTime begin, LocalDateTime end) {
        // 准备参数
        Map<String,Object> params = new HashMap<>();
        params.put("begin", begin);
        params.put("end", end);

        // 1. 获取待接单订单数，status = 2
        params.put("status", Orders.TO_BE_CONFIRMED);
        Integer ordersToBeConfirmed = reportMapper.getOrderCountByDate(params);
        if (ordersToBeConfirmed == null) {
            ordersToBeConfirmed = 0;
        }
        // 2. 获取待派送订单数，status = 3
        params.put("status", Orders.CONFIRMED);
        Integer ordersToBeDelivered = reportMapper.getOrderCountByDate(params);
        if (ordersToBeDelivered == null) {
            ordersToBeDelivered = 0;
        }
        // 3. 获取已完成订单数，status = 5
        params.put("status", Orders.COMPLETED);
        Integer completedOrders = reportMapper.getOrderCountByDate(params);
        if (completedOrders == null) {
            completedOrders = 0;
        }
        // 4. 获取已取消订单数，status = 6
        params.put("status", Orders.CANCELLED);
        Integer cancelledOrders = reportMapper.getOrderCountByDate(params);
        if (cancelledOrders == null) {
            cancelledOrders = 0;
        }
        // 5. 获取全部订单数
        params.remove("status");
        Integer totalOrders = reportMapper.getOrderCountByDate(params);
        if (totalOrders == null) {
            totalOrders = 0;
        }

        // 封装结果并返回
        return OrderOverViewVO.builder()
                .waitingOrders(ordersToBeConfirmed)
                .deliveredOrders(ordersToBeDelivered)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(totalOrders)
                .build();
    }

    /*
    * 获取菜品总览数据，包括已启售数量和已停售数量
    * @return DishOverViewVO
    * */
    @Override
    public DishOverViewVO getDishOverView() {
        // 查询已启售数量
        Integer enableDishCount = workspaceMapper.getDishCountByStatus(StatusConstant.ENABLE);
        if (enableDishCount == null) {
            enableDishCount = 0;
        }
        // 查询已停售数量
        Integer disableDishCount = workspaceMapper.getDishCountByStatus(StatusConstant.DISABLE);
        if (disableDishCount == null) {
            disableDishCount = 0;
        }
        // 封装结果并返回
        return DishOverViewVO.builder()
                .sold(enableDishCount)
                .discontinued(disableDishCount)
                .build();
    }

    /*
    * 获取菜品总览数据，包括已启售数量和已停售数量
    * @return DishOverViewVO
    * */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        // 查询已启售数量
        Integer enableSetmealCount = workspaceMapper.getSetmealCountByStatus(StatusConstant.ENABLE);
        if (enableSetmealCount == null) {
            enableSetmealCount = 0;
        }
        // 查询已停售数量
        Integer disableSetmealCount = workspaceMapper.getSetmealCountByStatus(StatusConstant.DISABLE);
        if (disableSetmealCount == null) {
            disableSetmealCount = 0;
        }
        // 封装结果并返回
        return SetmealOverViewVO.builder()
                .sold(enableSetmealCount)
                .discontinued(disableSetmealCount)
                .build();
    }
}
