package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
* 定时处理派送中的订单
* 处理时间间隙：每日凌晨1点执行一次
* 处理逻辑：
*  1. 订单的状态为 status == 4 (派送中),
*  2. 将订单状态更新为已完成 status == 5 (已完成)
* */
@Component
@Slf4j
public class ProcessDeliveryOrder {
    private final OrderMapper orderMapper;
    @Autowired
    public ProcessDeliveryOrder(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Scheduled(cron = " 0 0 1 * * ?") // 每日凌晨1点执行一次
    public void processDeliveryOrder() {
        log.info("Process delivery order task started, Now time: {}", LocalDateTime.now());
        LocalDateTime boarderTime = LocalDateTime.now().plusDays(-1); // 假设派送时间超过1天的订单需要处理
        List<Orders> orders = orderMapper.listByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, boarderTime);
        orders.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        });
    }

}
