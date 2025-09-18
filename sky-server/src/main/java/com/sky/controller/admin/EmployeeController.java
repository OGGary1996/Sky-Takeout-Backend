package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Tag(name = "Employee Management")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Operation(summary = "Employee Login", description = "Allows an employee to log in using their username and password.")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "EmployeeLoginDTO",
                    content = @Content(schema = @Schema(implementation = EmployeeLoginDTO.class)))
            @RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @Operation(summary = "Employee Logout", description = "Allows an employee to log out.")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /*
    * 新增员工
    * @param EmployeeDTO
    * @return
    * */
    @Operation(summary = "Add Employee", description = "Allows adding a new employee.")
    @PostMapping
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.insertEmployee(employeeDTO);
        return Result.success();
    }

    /*
    * 员工分页查询
    * @param EmployeePageQueryDTO
    * @return Result<PageResult>
    * */
    @Operation(summary = "Employee Pagination Query", description = "Allows querying employees with pagination.")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.selectPage(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 员工账号状态修改
    * @param status,id
    * @return Result<String>
    * */
    @Operation(summary = "Update Employee Status", description = "Allows updating the status of an employee account.")
    @PostMapping("/status/{status}")
    // 注意： @PathVariable注解用于从URL路径中提取变量，而@RequestParam注解用于从查询参数中提取变量
    public Result<String> accountStartStop(@PathVariable Integer status , @RequestParam Long id){
        log.info("员工账号状态修改：{},{}",status,id);
        employeeService.updateStatusById(status,id);
        return Result.success();
    }

    /*
    * 根据ID获取员工信息
    * 用于数据回显
    * @param id
    * @return Result<Employee>
    * */
    @Operation(summary = "Get Employee By ID", description = "Allows retrieving employee information by their ID.")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据ID获取员工信息：{}",id);
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }
    /*
    * 根据ID修改员工信息
    * @param EmployeeDTO
    * @return
    * */
    @Operation(summary = "Update Employee By ID", description = "Allows updating employee information by their ID.")
    @PutMapping
    public Result<String> accountInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("根据ID修改员工信息：{}",employeeDTO);
        employeeService.updateInfoById(employeeDTO);
        return Result.success();
    }

}
