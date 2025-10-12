package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Tag(name = "Admin Order Controller", description = "Admin Order Controller")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
    * 查询订单
    * @param OrdersPageQueryDTO
    * @return PageResult
    * */
    @GetMapping("/conditionSearch")
    @Operation(summary = "Condition Search", description = "Condition Search")
    public Result<PageResult> conditionSearch(@RequestBody OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("Condition search: {}", ordersPageQueryDTO);
        orderService.conditionSearch4Admin(ordersPageQueryDTO);
        return null;
    }

    /*
    * 统计各个状态的订单数量
    * @param 空
    * @return
    * */
    @GetMapping("/statistics")
    @Operation(summary = "Count of Orders by Status", description = "Count of Orders by Status")
    public Result<OrderStatisticsVO> statistics(){
        log.info("Count of orders by status");
        OrderStatisticsVO orderStatisticsVO = orderService.countByStatus();
        return Result.success(orderStatisticsVO);
    }

    /*
    * 查看订单详情
    * @pram Long id
    * @return
    * */
    @GetMapping("/details/{id}")
    @Operation(summary = "Order Details", description = "Order Details")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("Order details: {}", id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /*
    * 接单
    * @pram OrderConfirmDTO
    * @return
    * */
    @PutMapping("/confirm")
    @Operation(summary = "Confirm Order", description = "Confirm Order")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("Confirm order: {}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }


    /*
    * 拒绝订单
    * @param OrdersRejectionDTO
    * @return
    * */
    @PutMapping("/rejection")
    @Operation(summary = "Reject Order", description = "Reject Order")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("Reject order: {}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /*
    * 取消订单，逻辑与拒绝订单相同
    * @param OrdersCancelDTO
    * @return
    * */
    @PutMapping("/cancel")
    @Operation(summary = "Cancel Order", description = "Cancel Order")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("Cancel order: {}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /*
    * 派送订单，本质上与接单类似
    * @param Long id
    * @return
    * */
    @PutMapping("/delivery/{id}")
    @Operation(summary = "Deliver Order", description = "Deliver Order")
    public Result<String> delivery(@PathVariable Long id){
        log.info("Deliver order: {}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /*
    * 完成订单，本质上与派送订单类似
    * @param Long id
    * @return
    * */
    @PutMapping("/complete/{id}")
    @Operation(summary = "Complete Order", description = "Complete Order")
    public Result<String> complete(@PathVariable Long id){
        log.info("Complete order: {}", id);
        orderService.complete(id);
        return Result.success();
    }



}
