package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Tag(name = "AddressBookController", description = "Address Book Controller")
public class AddressBookController {
    private final AddressBookService addressBookService;
    @Autowired
    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    /*
    * 查询所有的地址信息
    * @param 空，当前用户Id在BaseContext中
    * @return List<AddressBook>
    * */
    @GetMapping("/list")
    @Operation(summary = "List All Address Information for Current User")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /*
    * 新增地址
    * @param AddressBook addressBook
    * @return
    * */
    @PostMapping
    @Operation(summary = "Add New Address")
    public Result<String> save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    /*
    * 根据id查询地址
    * @param Long id
    * @return AddressBook
    * */
    @GetMapping("/{id}")
    @Operation(summary = "Get Address by ID")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }
    /*
    * 修改地址
    * @param AddressBook addressBook
    * @return
    * */
    @PutMapping
    @Operation(summary = "Update Address by ID")
    public Result<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    /*
    * 设置默认地址
    * @param AddressBook addressBook
    * @return
    * */
    @PutMapping("/default")
    @Operation(summary = "Set Default Address")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /*
    * 删除地址
    * @param Long id
    * @return
    * */
    @DeleteMapping
    @Operation(summary = "Delete Address by ID")
    public Result<String> deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    /*
    * 查询默认地址，用于下单页面中
    * @param 空，当前用户Id在BaseContext中
    * @return AddressBook
    * */
    @GetMapping("default")
    @Operation(summary = "Get default address")
    public Result<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("No default address found");
    }

}
