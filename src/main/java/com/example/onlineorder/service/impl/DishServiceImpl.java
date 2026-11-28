package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.DishStatusEnum;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.DishMapper;
import com.example.onlineorder.mapper.OrderItemMapper;
import com.example.onlineorder.pojo.DTO.admin.DishDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateDishDTO;
import com.example.onlineorder.pojo.entity.Dish;
import com.example.onlineorder.pojo.entity.OrderItem;
import com.example.onlineorder.service.DishService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 菜品 Service 实现。
 */
@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 分页查询上架菜品，可按类别筛选，按 id 倒序。
     */
    @Override
    public Page<Dish> listOnSale(long page, long size, String category) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, DishStatusEnum.ON_SALE)
                .eq(StringUtils.hasText(category), Dish::getCategory, category)
                .orderByDesc(Dish::getDishId);
        return dishMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 按 id 查询单个菜品，不存在则抛业务异常。
     */
    @Override
    public Dish getById(Long id) {
        if (id == null) {
            throw new BusinessException("id 不能为空");
        }
        Dish result = dishMapper.selectById(id);
        if (result == null) {
            throw new BusinessException("未找到 id 为 " + id + " 的菜品");
        }
        return result;
    }

    /**
     * 分页查询所有菜品（含下架），按 id 倒序，供管理端使用。
     */
    @Override
    public Page<Dish> listAllDishes(long page, long size) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Dish::getDishId);
        return dishMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 新增菜品。
     * <p>
     * 先按「名称 + 分类」查重，避免同名同类菜品重复，再组装实体入库。
     */
    @Override
    public void addDish(DishDTO dto) {
        Long count = dishMapper.selectCount(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getName,dto.getName())
                .eq(Dish::getCategory, dto.getCategory()));
        if (count != null && count > 0) {
            throw new BusinessException("已存在同名同类菜品");
        }

        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setCategory(dto.getCategory());
        dish.setPrice(dto.getPrice());
        dish.setStock(dto.getStock());
        dish.setStatus(dto.getStatus());
        dishMapper.insert(dish);
    }

    /**
     * 修改菜品。
     * <p>
     * 字段为 null 表示不修改，配合 updateById 只更新非 null 字段实现部分更新。
     */
    @Override
    public void modifyDish(Long dishId, UpdateDishDTO dto) {
        if (dishId == null) {
            throw new BusinessException("菜品编号不能为空");
        }
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException("菜品不存在！");
        }
        dish.setName(dto.getName());
        dish.setCategory(dto.getCategory());
        dish.setPrice(dto.getPrice());
        dish.setStock(dto.getStock());
        dish.setStatus(dto.getStatus());
        dishMapper.updateById(dish);
    }

    /**
     * 删除菜品。
     * <p>
     * 上架的菜品不能删（需先下架），且被订单明细引用的菜品不能删（防外键约束）。
     */
    @Override
    public void deleteDish(Long dishId) {
        if (dishId == null) {
            throw new BusinessException("菜品编号不能为空");
        }
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException("菜品不存在！");
        }
        if (DishStatusEnum.ON_SALE.equals(dish.getStatus())) {
            throw new BusinessException("菜品在上架状态，无法删除！");
        }
        // 外键约束：被订单明细引用的菜品不能删（即使已下架）
        Long count = orderItemMapper.selectCount(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getDishId, dishId));
        if (count != null && count > 0) {
            throw new BusinessException("该菜品已被订单使用，无法删除！");
        }
        dishMapper.deleteById(dishId);
    }
}
