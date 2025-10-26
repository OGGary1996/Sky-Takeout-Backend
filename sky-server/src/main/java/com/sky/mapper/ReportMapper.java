package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    /*
    * 根据日期获取营业额
    * @param params 参数包含开始时间、结束时间和订单状态
    * @return Double 营业额
    * */
    Double getTurnoverByDate(Map<String, Object> params);

     /*
     * 根据日期获取新增/所有用户数
     * 通过动态SQL，动态判断传递begin和end参数，
     * 表示查询begin和end之间的（新增）用户，如果只传递end参数，表示查询end之前（所有）用户
     * @param params 参数包含日期
     * @return Integer 新增/总用户数
     * */
    Integer getUserByDate(Map<String, Object> params);


    /*
    * 根据日期查询当日/一段时间订单数
    * 通过动态sql，动态判断传递begin和end参数，
    * 如果传递begin和end参数为当天，则表示当天订单总数，如果还有一个status，则表示当天的有效订单数
    * 如果传递begin和end参数为一段时间，则表示该时间段内的订单总数，如果还有一个status，则表示该时间段内的有效订单数
    * @param params 参数包含开始时间、结束时间或者status
    * @return Integer 订单总数
    * */
    Integer getOrderCountByDate(Map<String, Object> params);


    /*
    * 查询销售前十的商品
    * @param params 参数包含开始日期和结束日期
    * @return List of Maps 包含商品名称和销量的列表
    * */
    List<GoodsSalesDTO> getSalesTop10ByDateRange(Map<String, Object> params);

}
