package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

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
    void save(AddressBook addressBook);

    /*
    * 根据id查询地址
    * @param Long id
    * @return AddressBook
    * */
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
    void setDefault(AddressBook addressBook);

    /*
    * 删除地址
    * @param Long id
    * @return
    * */
    void deleteById(Long id);


}
