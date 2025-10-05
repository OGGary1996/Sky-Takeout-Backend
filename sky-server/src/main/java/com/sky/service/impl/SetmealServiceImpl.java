package com.sky.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kezhang.aliyunossoperator.AliyunOssUtil;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final AliyunOssUtil aliyunOssUtil;
    private final DishMapper dishMapper;
    @Autowired
    public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper, AliyunOssUtil aliyunOssUtil, DishMapper dishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
        this.aliyunOssUtil = aliyunOssUtil;
        this.dishMapper = dishMapper;
    }

    /*
    * 新增套餐
    * @param SetmealDTO
    * @return
    * 注意：
    *  1. 前端需要显示categoryId和categoryName对应的下拉框，接口已经实现
    *  2. 前端需要显示dishId 和dishName对应的下拉框，接口已经实现
    *  3. 前端需要实现图片上传，接口已经实现
    *  4. 继续实现新增套餐功能
    * */
    @Transactional
    @Override
    public void insertSetmealWithDish(SetmealDTO setmealDTO) {
        // 1. 类型转换： SetmealDTO -> Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 2. 保存套餐基本信息到套餐表(setmeal),使用MyBatis中的generatedKeys获取自增主键id
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId(); // 获取套餐id
        // 3. 获取：List<SetmealDish> setmealDishes
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 4. 遍历：List<SetmealDish> setmealDishes，设置套餐id(setmealId)
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        // 5. 保存套餐和菜品的关联信息到套餐菜品关系表(setmeal_dish)
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /*
    * 条件分页查询
    * @param SetmealPageQueryDTO
    * @return PageResult
    * */
    @Override
    public PageResult selectPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 1. 开始分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 2. 执行查询setmeal
        Page<SetmealVO> setmealVOS = setmealMapper.selectPage(setmealPageQueryDTO);
        // 3. 封装并返回结果
        return PageResult.builder()
                .total(setmealVOS.getTotal())
                .records(setmealVOS.getResult())
                .build();

    }

    /*
    * 单个/批量删除套餐
    * @param List<Long> ids
    * @return
    * 注意：
    *  1. 如果status = 1 ，则不能删除
    *  2. 删除套餐之后，需要同时阐述setmeal_dish表中的数据
    *  3. 删除套餐后，需要同时删除阿里云OSS中的图片,需要在删除之前获取到image字段
    * */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        // 1. 判断是否在售
        Integer count = setmealMapper.countByIdsAndStatus(ids);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        // 2. 删除套餐，但是之前需要获取到images套餐
        List<String> images = setmealMapper.getImagesByIds(ids);
        setmealMapper.deleteByIds(ids);
        // 3.删除套餐和菜品的关联数据
        setmealDishMapper.deleteBySetmealIds(ids);
        // 4.删除阿里云OSS中的图片
        if (images != null && !images.isEmpty()){
            images.forEach(image -> {
                // 调用阿里云OSS的删除图片接口
                try{
                    aliyunOssUtil.deleteFile(image);
                }catch (ClientException e){
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /*
    * 根据id查询套餐信息和对应的菜品信息
    * @param Long id
    * @return SetmealVO
    * 注意：
    *  1. 需要查询套餐表(setmeal)和套餐菜品关系表(setmeal_dish)
    * */
    @Override
    public SetmealVO selectById(Long id) {
        // 1. 查询套餐表(setmeal)
        Setmeal setmeal = setmealMapper.selectById(id);
        // 2. 查询套餐菜品关系表(setmeal_dish)
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        // 3. 类型转换： Setmeal -> SetmealVO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }
    /*
    * 根据id修改套餐信息和对应的菜品信息
    * @param SetmealDTO
    * @return
    * 注意：
    *  1. 需要修改套餐表(setmeal)和套餐菜品关系
    * */
    @Transactional
    @Override
    public void updateSetmealWithDish(SetmealDTO setmealDTO) {
        // 1. 类型转换： SetmealDTO -> Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 2. 修改套餐表(setmeal)基本信息
        setmealMapper.updateById(setmeal);
        // 3. 更新套餐和菜品的关联信息
            // 3.1 删除套餐和菜品的关联数据
        Long setmealId = setmeal.getId();
        setmealDishMapper.deleteBySetmealIds(List.of(setmealId));
            // 3.2 新增套餐和菜品的关联数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /*
    * 停售/起售套餐
    * @param Integer status, Long id
    * @return
    * 注意：
    *  1. 停售套餐与dish是否启用无关，停售dish时，如果关联了套餐，则套餐会自动停售
    *  2. 启售套餐时，如果其中dish有停售的，则不能启售
    * */
    @Override
    public void setSetmealStatus(Integer status, Long id) {
        // 1. 判断停售或启售
        if (status == StatusConstant.ENABLE){
            // 2. 如果是启售套餐，先判断其中dish是否有停售的
            // 2.1 根据id获取到setmeal_dish表中的dish_id
            List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
            List<Long> dishIds = setmealDishes.stream().map(SetmealDish::getDishId).toList();
            // 2.2 根据dish_id查询dish表，判断是否存在停售的菜品
            Integer count = dishMapper.countByIdsAndNotStatus(dishIds);
            if (count > 0){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        // 3. 修改套餐的状态
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.updateById(setmeal);
    }

    /*
    * 根据categoryId查询所有在售套餐
    * @param Long categoryId
    * @return List<Setmeal>
    * 注意：
    *  1. 需要查询套餐表(setmeal)
    *  2. 只查询在售的套餐
    * */
    @Override
    public List<Setmeal> selectList(Long categoryId) {
        return setmealMapper.selectList(categoryId);
    }

    /*`
    * 根据套餐id查询包含的菜品列表
    * @param Long setmealId
    * @return List<DishItemVO>
    * 注意：
    *  1. 需要查询套餐菜品关系表(setmeal_dish)
    *  2. 需要查询菜品表(dish)，获取菜品的图片以及其他信息
    * */
    @Override
    public List<DishItemVO> getDishItemById(Long setmealId) {
          return setmealDishMapper.getDishItemBySetmealId(setmealId);
    }
}
