package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /*
    * 新增员工
    * @param EmployeeDTO
    * @return
    * */
    void insertEmployee(EmployeeDTO employeeDTO);

    /*
    * 员工分页查询
    * @param EmployeePageQueryDTO
    * @return PageResult
    * */
    PageResult selectPage(EmployeePageQueryDTO employeePageQueryDTO);

    /*
    * 员工状态修改
    * @param status,id
    * @return
    * */
    void updateStatusById(Integer status, Long id);

    /*
    * 根据ID获取员工信息
    * 用于数据回显
    * @param id
    * @return Employee
    * */
    Employee selectById(Long id);

    /*
    * 根据ID修改员工信息
    * @param EmployeeDTO
    * @return
    * */
    void updateInfoById(EmployeeDTO employeeDTO);
}
