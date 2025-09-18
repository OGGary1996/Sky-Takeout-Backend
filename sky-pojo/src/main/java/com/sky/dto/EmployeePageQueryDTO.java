package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Employee Page Query Data Transfer Object")
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    @Schema(description = "Employee Name")
    private String name;

    //页码
    @Schema(description = "Page Number", example = "1")
    private int page;

    //每页显示记录数
    @Schema(description = "Page Size", example = "10")
    private int pageSize;

}
