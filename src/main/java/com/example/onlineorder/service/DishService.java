package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.pojo.DTO.admin.DishDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateDishDTO;
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

    /**
     * 分页查询所有菜品（含下架），供管理端使用。
     *
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 菜品分页结果
     */
    Page<Dish> listAllDishes(long page, long size);

    /**
     * 新增菜品。
     *
     * @param dto 新增菜品请求体
     */
    void addDish(DishDTO dto);

    /**
     * 修改菜品（字段为 null 表示不修改）。
     *
     * @param dishId 菜品编号
     * @param dto    修改内容
     */
    void modifyDish(Long dishId, UpdateDishDTO dto);

    /**
     * 删除菜品。
     *
     * @param dishId 菜品编号
     */
    void deleteDish(Long dishId);
}
