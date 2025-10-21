package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /*
    * 插入订单
    * @pram order
    * @return
    * 注意：
    *  1.需要使用主键回填，以便获取插入的订单id
    *  2.后续使用order_detail表时需要使用订单id
    * */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /*
    * 查询历史订单，分页查询
    * @param OrdersPageQueryDTO
    * @return Page<Orders>
    * */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /*
    * 根据id查询订单
    * @pram Long id
    * @return Orders
    * */
    @Select("SELECT id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status FROM orders WHERE id = #{id}")
    Orders getById(Long id);

    /*
    * 统计各个状态的订单数量
    * @param status
    * @return Integer
    * */
    @Select("SELECT COUNT(id) FROM orders WHERE status = #{status}")
    Integer countByStatus(Integer status);

    /*
    * 根据订单状态和下单时间查询订单列表
    * 用于定时任务处理未支付订单
    * @param status， boarderTime
    * @return List<Orders>
    * */
    @Select("SELECT * FROM orders WHERE status = #{status} AND order_time <= #{boarderTime}")
    List<Orders> listByStatusAndOrderTime(Integer status, LocalDateTime boarderTime);
}
