package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {

    /*
    * 根据开始日期和结束日期获取营业额报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 营业额报表
    * 流程：
    *  1. 首先根据起止日期，将起止日期中的每一天保存为List
    *  2. 循环遍历日期List，每一次都根据日期获取到当天的营业额，并保存在List
    *  3. 如果存在营业额为null时，则转换为0.0金额
    *  4. 将日期List和营业额List转换为String，然后封装为VO
    * */
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);

    /*
    * 根据开始时间和结束时间获取用户报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 用户报表
    * */
    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    /*
    * 根据开始时间和结束时间获取订单报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 订单报表
    * */
    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);

    /*
    * 根据开始时间和结束时间获取销售前十报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 销售前十报表
    * */
    SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end);

    /*
    * 导出综合报表到Excel文件
    * @return void
    * 流程：
    *  1. 读取Excel模版文件 template/operation_report_template.xlsx
    *  2. 获取报表数据： 总： 营业额、有效订单数、订单完成率、平均单价、新增用户数；分：每天的数据
    *  3. 将数据写入到Excel文件的对应单元格中
    *  4. 设置响应头，返回Excel文件给前端进行下载
    * */
    void exportBusinessReport(HttpServletResponse response) throws IOException;

}
