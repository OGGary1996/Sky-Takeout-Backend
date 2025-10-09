package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /*
    * 查询所有的地址信息，默认地址排在最前
    * @param AddressBook addressBook,封装查询条件
    * @return List<AddressBook>
    * */
    List<AddressBook> list(AddressBook addressBook);

    /*
    * 新增地址
    * @param AddressBook addressBook
    * @return
    * 注意：新增的地址不需要判断是否为默认地址，默认地址的设置在修改地址接口中
    * */
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /*
    * 根据id查询地址
    * @param Long id
    * @return AddressBook
    * */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);
    /*
    * 根据id修改地址
    * @param AddressBook addressBook
    * @return
    * */
    void update(AddressBook addressBook);

    /*
    * 设置默认地址
    * @param AddressBook addressBook
    * @return
    * 注意：
    *  1. 需要将其与的他地址设置为非默认地址
    * */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /*
    * 删除地址
    * @param Long id
    * @return
    * */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);
}
