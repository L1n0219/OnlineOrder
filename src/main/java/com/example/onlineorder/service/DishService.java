package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.pojo.entity.Dish;

/**
 * 菜品 Service 接口。
 */
public interface DishService {

    /**
     * 分页查询上架菜品，可按类别筛选。
     */
    Page<Dish> listOnSale(long page, long size, String category);

    /**
     * 按 id 查询单个菜品。
     */
    Dish getById(Long id);
}
