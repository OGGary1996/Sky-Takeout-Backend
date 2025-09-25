package com.sky.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kezhang.aliyunossoperator.AliyunOssUtil;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final AliyunOssUtil aliyunOssUtil;
    private final SetmealMapper setmealMapper;

    public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper, SetmealDishMapper setmealDishMapper, AliyunOssUtil aliyunOssUtil, SetmealMapper setmealMapper) {
        this.dishMapper = dishMapper;
        this.dishFlavorMapper = dishFlavorMapper;
        this.setmealDishMapper = setmealDishMapper;
        this.aliyunOssUtil = aliyunOssUtil;
        this.setmealMapper = setmealMapper;
    }

    /*
    * 新增菜品
    * @param DishDto dishDto
    * @return
    * 注意：image字段由前端传递，后端不需要负责image字段的上传和下载
    *
    * 事务控制：
    *  1. 同时需要操作两个Mapper：DishMapper,DishFlavorMapper
    *  2. DishMapper操作dish表，DishFlavorMapper操作dish_flavor
    *  3. 需要保证两张表的数据一致性
    *
    * 主键回填：
    *  1. 插入数据到dish_flavor时，需要字段dish_id
    *  2. dish_id是dish表的主键，但是在插入dish时，还没有插入到数据库，主键id还没生成
    *  3. 需要在DishMapper的insert方法中，使用MyBatis的useGeneratedKeys属性，主键回填
    *  4. 然后在service层代码中，获取到Dish的id，设置到每个DishFlavor对象的dishId字段中
    * */
    @Transactional
    @Override
    public void insertWithFlavor(DishDTO dishDTO) {
        // 1. 操作dish表，保存菜品的基本信息
            // 1.1 DTO对象转换为Entity对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
            // status字段默认为1（起售状态）
            // 1.2 保存基本信息到菜品表dish
        dishMapper.insert(dish);
            // 1.3 获取菜品id
        Long dishId = dish.getId();

        // 2. 操作dish_flavor表，保存菜品的口味信息
            // 2.1 获取菜品口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){ // 防止空指针异常
            // 2.2 设置dishId
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 2.3 保存菜品口味数据到菜品口味表dish
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /*
    * 菜品条件分类查询
    * @param DishPageQueryDTO dishPageQueryDTO
    * @Return Result<PageResult>
    * */
    @Override
    public PageResult selectPage(DishPageQueryDTO dishPageQueryDTO) {
        // 1. 使用PageHelper进行分页查询
            // 1.1 开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
            // 1.2 执行查询,并使用Page<DishVO>对象接收结果
        Page<DishVO> page = dishMapper.selectPage(dishPageQueryDTO);

        // 2. 获取分页查询结果
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }

    /*
    * 批量/单个删除菜品
    * @param List<Long> ids
    * @Return Result<String>
    * 注意：
    *  1. 如果菜品status = 1 (启售状态)，则不能删除
    *  2. 如果菜品关联了setmeal,则不能删除，涉及到setmeal_dish表
    *  3. 删除菜品之后，需要删除dish_flavor表中的数据
    *  4. 删除菜品之后，需要删除阿里云OSS中的图片
    *     注意：需要在删除菜品之前拿到image字段，删除之后无法获取
    * */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids){
        // 1. 判断是否在售: 查询count(*) from dish where id in (1,2,3) and status = 1
        // 如果数量存在，则抛出业务异常
        if (dishMapper.countByIdsAndStatus(ids) != 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        // 2. 判断是否关联了套餐: 查询count(*) from setmeal_dish where dish_id in (1,2,3)
        // 如果数量存在，则抛出业务异常
        if (setmealDishMapper.countByDishIds(ids) != 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 3. 删除菜品
        // 3.1 获取image字段，删除图片
        List<String> images = dishMapper.getImagesByIds(ids);
        // 3.2 删除菜品数据
        dishMapper.deleteBatch(ids);
        // 4. 删除菜品口味数据
        dishFlavorMapper.deleteByDishIds(ids);
        // 5. 删除阿里云OSS中的图片
        images.forEach(image -> {
            try {
                aliyunOssUtil.deleteFile(image);
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*
    * 根据id查询dish + dish_flavor,用于修改菜品时回显数据
    * @param Long id
    * @Return DishVO
    * 注意：
    *  1. 需要同时操作两张表：dish,dish_flavor
    * */
    @Override
    public DishVO selectByIdWithFlavor(Long id) {
        // 1. 查询dish表，获取到基本信息
        Dish dish = dishMapper.selectById(id);
        // 2. 查询dish_flavor表，获取到口味信息
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        // 3. 封装到DishVO对象中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    /*
    * 根据id修改菜品
    * @param DishDTO dishDTO
    * @Return
    * 注意：
    *  1. 需要同时操作两张表：dish,dish_flavor
    *  2. 对于dish_flavor表，先删除原有口味数据，再插入口味数据，保证数据一致性
    * */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 1. 更新dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        // 2. 先删除，再插入dish_flavor表的数据
        // 2.1 删除原有口味数据(和批量删除ids共用)
        List<Long> ids = List.of(dishDTO.getId());
        dishFlavorMapper.deleteByDishIds(ids);
        // 2.2 插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 防止空指针异常
        if (flavors != null && !flavors.isEmpty()){
            // 设置dishId
            Long dishId = dishDTO.getId();
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 批量插入
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /*
    * 根据id修改菜品启售状态
    * @param Integer status, Long id
    * @return
    * 注意：
    *  1. 如果是停售操作，需要检查setmeal_dish表，是否关联了套餐
    *  2. 如果关联了setmeal，则整个setmeal都需要停售
    *  3. 涉及到setmeal表和setmeal_dish表
    * */
    @Transactional
    @AutoFill(OperationType.UPDATE)
    @Override
    public void updateStatusById(Integer status, Long id) {
        // 1. 封装Dish对象
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        // 2. 更新菜品状态,通用修改方法
        dishMapper.updateById(dish);
        // 3. 如果是停售操作，需要检查setmeal_dish表，是否关联了套餐
        if (status == StatusConstant.DISABLE){
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(id);
            // 3.1 如果关联了套餐，则整个setmeal都需要停售
            if (setmealIds != null && !setmealIds.isEmpty()){
                setmealMapper.setmealStopBatch(setmealIds);
            }
        }
    }

    /*
    * 显示所有在售的dish
    * @param Long categoryId，非必须，可以根据分类id查询
    * @Return List<DishVO>
    * */
    @Override
    public List<DishVO> listByCategoryId(Long categoryId) {
       return dishMapper.listByCategoryId(categoryId);
    }
}
