package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Employee Data Transfer Object")
public class EmployeeDTO implements Serializable {
    @Schema(description = "Employee ID")
    private Long id;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Employee Name")
    private String name;

    @Schema(description = "Phone Number")
    private String phone;

    @Schema(description = "Gender")
    private String sex;

    @Schema(description = "ID Number")
    private String idNumber;

}
