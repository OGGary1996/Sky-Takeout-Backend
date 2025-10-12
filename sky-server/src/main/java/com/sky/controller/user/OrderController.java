package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "User Order Controller", description = "User Order Controller")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
    * 提交订单
    * @pram ordersSubmitDTO
    * @return OrderSubmitVO
    * */
    @PostMapping("/submit")
    @Operation(summary = "Submit Order", description = "Submit Order")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("Submitting order: {}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @Operation(summary = "Order Payment", description = "Order Payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /*
    * 查询历史订单，分页查询
    * @param page, pageSize, status
    * @return PageResult
    * */
    @GetMapping("/historyOrders")
    @Operation(summary = "Get History Orders", description = "Get History Orders")
    public Result<PageResult> pageHistoryOrders(@RequestParam int page, @RequestParam int pageSize, @RequestParam(required = false) Integer status){
        log.info("Querying history orders: page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = orderService.pageQuery4User(page,pageSize, status);
        return Result.success(pageResult);
    }

    /*
    * 用户端查询订单详情
    * @pram Long id
    * @return
    * */
    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "Get Order Detail", description = "Get Order Detail" )
    public Result<OrderVO> orderDetails(@PathVariable Long id){
        log.info("Querying order details for order id: {}", id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /*
    * 用户端取消订单
    * @param Long id
    * @return
    * */
    @PutMapping("/cancel/{id}")
    @Operation(summary = "Cancel Order", description = "Cancel Order" )
    public Result<String> cancelOrder(@PathVariable Long id) throws Exception {
        log.info("Cancelling order with id: {}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    /*
    * 用户端再来一单
    * @param Long id
    * @return
    * */
    @PostMapping("/repetition/{id}")
    @Operation(summary = "Repetition Order", description = "Repetition Order" )
    public Result<String> repetitionOrder(@PathVariable Long id)  {
        log.info("Repetition order with id: {}", id);
        orderService.repetition(id);
        return Result.success();
    }
}
