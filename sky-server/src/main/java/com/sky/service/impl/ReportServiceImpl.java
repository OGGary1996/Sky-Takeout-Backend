package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;
    private final WorkspaceService workspaceService;
    @Autowired
    public ReportServiceImpl(ReportMapper reportMapper, WorkspaceService workspaceService) {
        this.reportMapper = reportMapper;
        this.workspaceService = workspaceService;
    }

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
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 创建日期和营业额的List
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();

        // 遍历起止日期之间的每一天，并保存到dateList中，并且查询每天的营业额，保存到turnoverList中
        while (!begin.isEqual(end)) {
            // 加入日期List
            dateList.add(begin);
            // 查询当天的营业额
            LocalDateTime beginOfDay = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(begin, LocalTime.MAX);
            Map<String,Object> params = new HashMap<>();
            params.put("begin", beginOfDay);
            params.put("end", endOfDay);
            params.put("status", Orders.COMPLETED);
            Double turnoverByDate = reportMapper.getTurnoverByDate(params);
            // 注意：MySQL中查询为空的营业额返回为null，需要手动处理
            if (turnoverByDate == null){
                turnoverByDate = 0.0;
            }
            // 加入营业额List
            turnoverList.add(turnoverByDate);

            begin = begin.plusDays(1);
        }
        // 处理日期List和营业额List，转换为String, 以逗号分隔；StringUtils from Apache Commons Lang3
        String dateString = StringUtils.join(dateList, ",");
        String turnoverString = StringUtils.join(turnoverList, ",");

        // 封装为VO并返回
        return TurnoverReportVO.builder()
                .dateList(dateString)
                .turnoverList(turnoverString)
                .build();
    }

    /*
    * 根据开始时间和结束时间获取用户报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 用户报表
    * */
    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        // 创建日期和用户数的List
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        // 遍历起止日期之间的每一天，并保存到dateList中，并且查询每天的新增用户数和总用户数，保存到对应的List中
        while (!begin.isEqual(end)) {
            // 加入日期List
            dateList.add(begin);
            // 查询当天的新增用户数和总用户数
            LocalDateTime beginOfDay = LocalDateTime.of(begin,LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(begin,LocalTime.MAX);
            Map<String,Object> params = new HashMap<>();
              // 1. 查询新增用户（传递begin和end）
            params.put("begin", beginOfDay);
            params.put("end", endOfDay);
            Integer increaseUser = reportMapper.getUserByDate(params);
            if (increaseUser == null) {
                increaseUser = 0;
            }
             // 2. 查询总用户数（只传递end）
            params.remove("begin");
            Integer totalUser = reportMapper.getUserByDate(params);
            // 注意：MySQL中查询为空的用户数返回为null，需要手动处理
            if (totalUser == null) {
                totalUser = 0;
            }
            // 加入List
            newUserList.add(increaseUser);
            totalUserList.add(totalUser);

            begin = begin.plusDays(1);
        }
        // 处理日期List、新增用户List和总用户List，转换为String, 以逗号分隔；StringUtils from Apache Commons Lang3
        String dateString = StringUtils.join(dateList, ",");
        String newUserString = StringUtils.join(newUserList, ",");
        String totalUserString = StringUtils.join(totalUserList, ",");
        // 封装为VO并返回
        return UserReportVO.builder()
                .dateList(dateString)
                .newUserList(newUserString)
                .totalUserList(totalUserString)
                .build();
    }

    /*
    * 根据开始时间和结束时间获取订单报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 订单报表
    * */
    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        // 创建日期和订单数的List
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        // 查询起止日期之间的总订单数和有效订单数
        LocalDateTime beginOfPeriod = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endOfPeriod = LocalDateTime.of(end, LocalTime.MAX);
        Map<String,Object> paramsByPeriod = new HashMap<>();
         // 1. 查询总订单数
        paramsByPeriod.put("begin", beginOfPeriod);
        paramsByPeriod.put("end", endOfPeriod);
        Integer totalOrderCount = reportMapper.getOrderCountByDate(paramsByPeriod);
        if (totalOrderCount == null) {
            totalOrderCount = 0;
        }
         // 2.查询有效订单数
        paramsByPeriod.put("status",Orders.COMPLETED);
        Integer validOrderCount = reportMapper.getOrderCountByDate(paramsByPeriod);
        if (validOrderCount == null) {
            validOrderCount = 0;
        }
         // 3. 计算订单完成率
        Double orderCompletionRate;
        if (totalOrderCount == 0) {
            orderCompletionRate = 0.0;
        } else {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }


        // 遍历起止日期之间的每一天，并保存到dateList中，并且查询每天的订单数和有效订单数，保存到对应的List中
        while (!begin.isEqual(end)){
            // 加入日期List
            dateList.add(begin);
            // 查询当天的订单数和有效订单数
            LocalDateTime beginOfDay = LocalDateTime.of(begin,LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(begin,LocalTime.MAX);
            Map<String,Object> paramsByDate = new HashMap<>();
             // 1. 查询当天的订单总数
            paramsByDate.put("begin", beginOfDay);
            paramsByDate.put("end", endOfDay);
            Integer totalOrderCoundByDay = reportMapper.getOrderCountByDate(paramsByDate);
            if (totalOrderCoundByDay == null) {
                totalOrderCoundByDay = 0;
            }
             // 2. 查询当天的有效订单数
            paramsByDate.put("status", Orders.COMPLETED);
            Integer validOrderCountByDay = reportMapper.getOrderCountByDate(paramsByDate);
            if (validOrderCountByDay == null) {
                validOrderCountByDay = 0;
            }
            // 加入List
            orderCountList.add(totalOrderCoundByDay);
            validOrderCountList.add(validOrderCountByDay);

            begin = begin.plusDays(1);
        }
        // 处理日期List、订单数List和有效订单数List，转换为String, 以逗号分隔；StringUtils from Apache Commons Lang3
        String dateString = StringUtils.join(dateList, ",");
        String orderCountString = StringUtils.join(orderCountList, ",");
        String validOrderCountString = StringUtils.join(validOrderCountList, ",");

        // 封装为VO并返回
        return OrderReportVO.builder()
                .dateList(dateString)
                .orderCountList(orderCountString)
                .validOrderCountList(validOrderCountString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /*
    * 根据开始时间和结束时间获取销售前十报表
    * @param begin 开始日期
    * @param end 结束日期
    * @return 销售前十报表
    * */
    @Override
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {
        // 创建销售数量和商品名称的List
        List<Integer> salesNumberList = new ArrayList<>();
        List<String> salesNameList = new ArrayList<>();
        LocalDateTime beginOfPeriod = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endOfPeriod = LocalDateTime.of(end, LocalTime.MAX);

        // 查询销售前十的商品,得到的是List of GoodsSalesDTO
        Map<String,Object> params = new HashMap<>();
        params.put("begin", beginOfPeriod);
        params.put("end", endOfPeriod);
        List<GoodsSalesDTO> salesTop10ByDateRange = reportMapper.getSalesTop10ByDateRange(params);
        if (salesTop10ByDateRange == null) {
            salesTop10ByDateRange = new ArrayList<>();
        }
        // 遍历结果，将商品名称和销售数量分别保存到对应的List中
        salesTop10ByDateRange.forEach(salesTop10 -> {
            salesNameList.add(salesTop10.getName());
            salesNumberList.add(salesTop10.getNumber());
        });

        // 处理商品名称List和销售数量List，转换为String, 以逗号分隔；StringUtils from Apache Commons Lang3
        String salesNumberString = StringUtils.join(salesNumberList, ",");
        String salesNameString = StringUtils.join(salesNameList, ",");

        // 封装为VO并返回
        return SalesTop10ReportVO.builder()
                .numberList(salesNumberString)
                .nameList(salesNameString)
                .build();
    }

    /*
    * 导出综合报表到Excel文件
    * @return void
    * 流程：
    *  1. 读取Excel模版文件 template/operation_report_template.xlsx
    *  2. 获取报表数据： 总： 营业额、有效订单数、订单完成率、平均单价、新增用户数；分：每天的数据
    *  3. 将数据写入到Excel文件的对应单元格中
    *  4. 设置响应头，返回Excel文件给前端进行下载
    * */
    @Override
    public void exportBusinessReport(HttpServletResponse response) throws IOException {
        // 1. 读取Excel模版文件 template/operation_report_template.xlsx
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/operation_report_template.xlsx");
          // 1.1 创建Workbook对象
        Workbook workbook = WorkbookFactory.create(inputStream);
          // 1.2 获取sheet
        Sheet sheet = workbook.getSheetAt(0);

        // 2. 读取报表数据： 总： 营业额、有效订单数、订单完成率、平均单价、新增用户数；分：每天的数据
        // 相应的实现在工作台模块中已经完成，直接调用即可,WorkspaceService.getTodayData,传递时间区间即可
          // 2.1 获取30天数据
        LocalDateTime begin = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);
        BusinessDataVO monthData = workspaceService.getTodayData(begin, end);
          // 2.2 获取当日明细数据
        List<LocalDateTime> dateList = new ArrayList<>();
        List<BusinessDataVO> dailyDataList = new ArrayList<>();
        while(!begin.isAfter(end)) {
            dateList.add(begin);
            LocalDateTime beginOfDay = begin.with(LocalTime.MIN);
            LocalDateTime endOfDay = begin.with(LocalTime.MAX);
            BusinessDataVO todayData = workspaceService.getTodayData(beginOfDay, endOfDay);
            dailyDataList.add(todayData);
            begin = begin.plusDays(1);
        }

        // 3. 将数据写入到Excel文件的对应单元格中
          // 3.1 写入日期区间
        sheet.getRow(1).getCell(1).setCellValue(begin.toLocalDate().toString() + " ~ " + end.toLocalDate().toString());
          // 3.2 写入30天数据
        sheet.getRow(3).getCell(2).setCellValue(monthData.getTurnover());
        sheet.getRow(3).getCell(4).setCellValue(monthData.getOrderCompletionRate());
        sheet.getRow(3).getCell(6).setCellValue(monthData.getNewUsers());
        sheet.getRow(4).getCell(2).setCellValue(monthData.getValidOrderCount());
        sheet.getRow(4).getCell(4).setCellValue(monthData.getUnitPrice());
          // 循环写入每天数据，从第7行开始
        for (int i = 0 ; i < dateList.size() ; i++) {
            int rowIndex = i + 7;
            LocalDateTime dateTime = dateList.get(i);
            BusinessDataVO dailyData = dailyDataList.get(i);
            sheet.getRow(rowIndex).getCell(1).setCellValue(dateTime.toLocalDate().toString());
            sheet.getRow(rowIndex).getCell(2).setCellValue(dailyData.getTurnover());
            sheet.getRow(rowIndex).getCell(3).setCellValue(dailyData.getValidOrderCount());
            sheet.getRow(rowIndex).getCell(4).setCellValue(dailyData.getOrderCompletionRate());
            sheet.getRow(rowIndex).getCell(5).setCellValue(dailyData.getUnitPrice());
            sheet.getRow(rowIndex).getCell(6).setCellValue(dailyData.getNewUsers());
        }

        // 4. 返回Excel文件给前端进行下载
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        // 5. 关闭资源
        outputStream.close();
        workbook.close();
        inputStream.close();
    }
}
