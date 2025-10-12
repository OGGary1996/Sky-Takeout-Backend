package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /*
    * 批量插入订单明细
    * @pram List<OrderDetail> orderDetailList
    * @return
    * */
    void insertBatch(List<OrderDetail> orderDetailList);

    /*
    * 根据订单id查询订单明细
    * @pram orderId
    * @return List<OrderDetail>
    * */
    @Select("SELECT id, name, image, order_id, dish_id, setmeal_id, dish_flavor, number, amount FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
