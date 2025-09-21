package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /*
    * 新增员工
    * @param Employee
    * @return
    * */
    @AutoFill(OperationType.INSERT)
    @Insert("INSERT INTO employee (username,name,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user) " +
            "VALUES" +
            " ( #{username}, #{name}, #{password}, #{phone},#{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertEmployee(Employee employee);

    /*
    * 员工分页查询
    * @param EmployeePageQueryDTO
    * @return
    * */
    // 需要使用动态sql，所以这里不使用注解方式，详见EmployeeMapper.xml
    Page<Employee> selectPage(EmployeePageQueryDTO employeePageQueryDTO);

    /*
    * 员工信息修改
    * 注意：这里的修改是统一操作，通过动态SQL实现
    * 员工状态的任何字段，都通过这个方法操作
    * @param Employee
    * @return
    * */
    @AutoFill(OperationType.UPDATE)
    void updateById(Employee employee);

    /*
    * 根据ID获取员工信息
    * 用于数据回显
    * @param id
    * */
    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee selectById(Long id);
}
