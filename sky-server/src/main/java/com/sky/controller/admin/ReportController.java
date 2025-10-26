package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Tag(name = "Admin Report Controller", description = "Admin Report Controller")
public class ReportController {
    private final ReportService reportService;
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/turnoverStatistics")
    @Operation(summary = "Get Turnover Statistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("Get Turnover Statistics: begin={}, end={}", begin, end);
        TurnoverReportVO turnoverReport = reportService.getTurnoverReport(begin, end);
        return Result.success(turnoverReport);
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "Get User Statistics")
    public Result<UserReportVO> userStatistics(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("Get User Statistics: begin={}, end={}", begin, end);
        UserReportVO userReport = reportService.getUserReport(begin, end);
        return Result.success(userReport);
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "Get Order Statistics")
    public Result<OrderReportVO> orderStatistics(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("Get Order Statistics: begin={}, end={}", begin, end);
        OrderReportVO orderReport = reportService.getOrderReport(begin, end);
        return Result.success(orderReport);
    }

    @GetMapping("/top10")
    @Operation(summary = "Get Top 10 Sales")
    public Result<SalesTop10ReportVO> top10(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("Get top 10 sales: begin={}, end={}", begin, end);
        SalesTop10ReportVO salesTop10Report = reportService.getSalesTop10Report(begin, end);
        return Result.success(salesTop10Report);
    }

    /*
    * 导出报表
    * @param 空 ，默认30天内
    * @return 空 ， 下载文件
    * */
    @GetMapping("/export")
    @Operation(summary = "Export Report")
    public void exportReport(HttpServletResponse response) throws IOException {
        log.info("Export Report ... ");
        reportService.exportBusinessReport(response);
    }
}
