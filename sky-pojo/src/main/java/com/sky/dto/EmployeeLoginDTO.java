package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Employee Login Data Transfer Object")
public class EmployeeLoginDTO implements Serializable {

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Password(not encrypted)")
    private String password;

}
