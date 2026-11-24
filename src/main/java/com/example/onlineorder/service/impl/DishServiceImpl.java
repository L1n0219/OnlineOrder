package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.DishStatusEnum;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.DishMapper;
import com.example.onlineorder.pojo.entity.Dish;
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
}
