package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /*
    * 提交订单
    * @pram ordersSubmitDTO
    * @return OrderSubmitVO
    * 注意：
    *  1. 需要操作 order，order_detail，address_book，shopping_cart 四张表
    *  2. 向订单表插入一条数据，结合address_book表和传递的参数
    *  3. 向订单明细表插入多条数据，结合shopping_cart表
    *  4. 清空用户购物车
    *  5. 封装并返回 OrderSubmitVO 对象
    * */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /*
    * 查询历史订单，分页查询
    * @param page, pageSize, status
    * @return PageResult
    * */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /*
    * 查询订单详情
    * @pram Long id
    * @return OrderVO
    * */
    OrderVO getOrderDetails(Long id);

    /*
    * 用户取消订单
    * @pram Long id
    * @return
    * 注意：
    *  1.如果订单状态为带支付，status = 1，则直接修改订单状态为取消
    *  2.如果订单状态为已支付，status = 2，则修改订单状态之后需要调用微信支付的退款接口
    *  3.如果订单状态为已接点、已派送，status = 3、4，需要练习商家，在admin端进行取消订单
    *  4.如果订单状态为已完成、已取消，status = 5、6，不能取消订单
    * */
    void userCancelById(Long id) throws Exception;

    /*
    * 用户点击再来一单
    * @pram Long id
    * @return
    * 注意：
    *  1.再来一单的本质上是将之前的订单数据重新插入到购物车表
    *  2.需要查询订单的详细信息，order_detail表
    *  3.将订单明细数据插入到购物车表, shopping_cart表
    * */
    void repetition(Long id);

    /*
    * 管理端条件查询订单
    * @pram OrdersPageQueryDTO
    * @return PageResult
    * 注意：
    *  1. 首先根据查询条件获得List<Orders>
    *  2. 需要将List<Orders>转换为List<OrderVO>,并填充上orderDishes属性
    *  3. 将List<OrderVO>封装到PageResult并返回
    * */
    PageResult conditionSearch4Admin(OrdersPageQueryDTO ordersPageQueryDTO);

    /*
    * 分别统计不同状态下的订单数量
    * @pram
    * @return OrderStatisticsVO
    * 注意：主要统计的：status = 2 待接单，status = 3 已接单，status = 4 派送中
    * */
    OrderStatisticsVO countByStatus();

    /*
    * 管理端接单
    * @pram OrdersConfirmDTO
    * @return
    * 接单的本质是修改订单状态为 status = 3 已接单
    * */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /*
    * 管理端拒绝订单
    * @pram OrdersRejectionDTO
    * @return
    * 注意：
    *  1.拒绝订单的本质上是修改订单状态为 status = 6 已取消
    *  2.需要拒绝理由
    *  3.如果订单的状态为已支付 status = 2，则需要调用微信支付的退款接口
    *  4.只能订单状态为待接单 status = 2
    * */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /*
    * 管理端取消订单
    * @pram OrdersCancelDTO
    * @return
    * 注意：
    *  1.取消订单的本质上是修改订单状态为 status = 6 已取消
    *  2.需要取消理由
    *  3.如果订单的状态为已支付 status = 2，则需要调用微信支付的退款接口
    *  4.只能订单状态为待接单 status = 2，已接单 status = 3，派送中 status = 4
    * */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /*
    * 管理端派送订单,本质上与接单类似
    * @pram Long id
    * @return
    * 注意：
    *  1. 派送订单的本质是修改订单状态为 status = 4 派送中
    *  2. 只有订单状态为已接单 status = 3 才能派送
    * */
    void delivery(Long id);

    /*
    * 管理端完成订单
    * @pram Long id
    * @return
    * 注意：
    *  1. 完成订单的本质是修改订单状态为 status = 5 已完成
    *  2. 只有订单状态为派送中 status = 4 才能完成订单
    * */
    void complete(Long id);
}
