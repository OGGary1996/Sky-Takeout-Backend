package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 账号存在，进行密码比对
        // 1. 首先获取到数据库中存储的加密后的密码
        String encryptedPassword = employee.getPassword();
        // 2. 对前端传递过来的明文密码进行加密，使用Spring提供的DigestUtils工具类,进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(encryptedPassword)){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 密码比对通过，继续判断账号状态
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 密码比对通过，账户状态通过，登录成功
        return employee;
    }

    /*
    * 新增员工
    * @param EmployeeDTO
    * @return
    * */
    @Override
    public void insertEmployee(EmployeeDTO employeeDTO) {
        // 1. 首先进行DTO -> Entity的属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 2. 调用常量类StatusConstant，设置账号状态
        employee.setStatus(StatusConstant.ENABLE);
        // 3. 调用常量类PasswordConstant常量类设置初始密码，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 4. 设置其他的属性，创建时间、更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 5. 创建人、修改人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        // 6. 调用Mapper，执行插入操作
        employeeMapper.insertEmployee(employee);
    }

    /*
    * 员工分页查询
    * @param EmployeePageQueryDTO
    * @return PageResult
    * */
    @Override
    public PageResult selectPage(EmployeePageQueryDTO employeePageQueryDTO) {
        // 1.开始分页
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 2.调用Mapper，执行分页查询
        Page<Employee> page = employeeMapper.selectPage(employeePageQueryDTO);
        // 3.封装并返回结果
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }

    /*
    * 员工状态修改
    * @param status,id
    * @return
    * */
    @Override
    public void updateStatusById(Integer status, Long id) {
        // 注意：除了更新状态之外，还需要更新修改时间、修改人，所以需要进行属性的封装
        // 把参数封装为Employee对象
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        employeeMapper.updateById(employee);
    }

    /*
    * 根据ID获取员工信息
    * 用于数据回显
    * @param id
    * @return Employee
    * */
    @Override
    public Employee selectById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        // 注意：需要隐藏密码字段
        employee.setPassword("******"); // 将密码设置为不可见
        return employee;
    }
    /*
    * 根据ID修改员工信息
    * @param EmployeeDTO
    * @return
    * */
    @Override
    public void updateInfoById(EmployeeDTO employeeDTO) {
        // 1. 首先进行DTO -> Entity的属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 2. 设置其他的属性，更新时间 、修改人
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        // 3. 调用Mapper，执行更新操作
        employeeMapper.updateById(employee);
    }
}
