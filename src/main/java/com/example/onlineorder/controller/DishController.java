package com.example.onlineorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.entity.Dish;
import com.example.onlineorder.service.DishService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品相关接口，统一前缀 /api/dishes。
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 分页查询上架菜品（可按类别筛选）。
     */
    @GetMapping
    public Result<PageResult<Dish>> getAllDishes(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(defaultValue = "") String category) {
        Page<Dish> result = dishService.listOnSale(page, size, category);
        return Result.ok("成功获取菜品列表", new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 按 id 查询单个菜品。
     */
    @GetMapping("/{id}")
    public Result<Dish> getDishById(@PathVariable Long id) {
        return Result.ok("成功查找到 id 为 " + id + " 的菜品", dishService.getById(id));
    }
}
