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
* 定时处理未支付订单
* 处理时间间隙：每分钟执行一次
* 处理逻辑：
*  1. 订单未支付时间超过15分钟
*  2. 订单的状态为status == 1 (待支付),
*  3. 此时pay_status == 0 (未支付)，并且在支付和退款时会自动修改pay_status的值，此处不需要考虑pay_status
*  4. 将订单状态更新为已取消 status == 6 (已取消)
* */
@Component
@Slf4j
public class CancelOrderNotPay {
    private final OrderMapper orderMapper;
    @Autowired
    public CancelOrderNotPay(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次
    public void cancelOrderNotPay() {
        log.info("Cancel order task started, Now time: {}", LocalDateTime.now());
        LocalDateTime boarderTime = LocalDateTime.now().plusMinutes(-15); // 未支付超过15分钟
        List<Orders> orders = orderMapper.listByStatusAndOrderTime(Orders.UN_PAID, boarderTime);
        orders.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("Order not paid in time, system auto cancelled.");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        });
    }
}
