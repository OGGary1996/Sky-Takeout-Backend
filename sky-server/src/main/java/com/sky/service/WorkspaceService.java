package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {
    /*
    * 获取今日数据，包括营业额、有效订单数、订单完成率、平均客单价、新增用户数
    * @return BusinessDataVO
    * */
    BusinessDataVO getTodayData(LocalDateTime begin, LocalDateTime end);

    /*
    * 获取订单管理数据，包括各个状态的订单数量：待接单、待派送、已完成、已取消、全部订单
    * @return OrderOverViewVO
    * */
    OrderOverViewVO getOrderOverView(LocalDateTime begin, LocalDateTime end);

    /*
    * 获取菜品总览数据，包括已启售数量和已停售数量
    * @return DishOverViewVO
    * */
    DishOverViewVO getDishOverView();

    /*
    * 获取套餐总览数据，包括已启售数量和已停售数量
    * @return SetmealOverViewVO
    * */
    SetmealOverViewVO getSetmealOverView();
}
