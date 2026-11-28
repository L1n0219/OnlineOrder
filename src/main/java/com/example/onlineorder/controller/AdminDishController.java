package com.example.onlineorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.admin.DishDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateDishDTO;
import com.example.onlineorder.pojo.entity.Dish;
import com.example.onlineorder.service.DishService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-菜品管理接口，统一前缀 /api/admin/dishes。
 * <p>
 * 该路径下的接口会被 Sa-Token 拦截器要求「登录 + admin 角色」。
 */
@RestController
@RequestMapping("/api/admin/dishes")
public class AdminDishController {

    @Resource
    private DishService dishService;

    /**
     * 分页查询所有菜品（含下架），供管理端查看。
     */
    @GetMapping
    public Result<PageResult<Dish>> getAllDishes(@RequestParam(defaultValue = "1") Long page,
                                                 @RequestParam(defaultValue = "10") Long size) {
        Page<Dish> result = dishService.listAllDishes(page, size);
        return Result.ok("成功获取菜品列表", new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 新增菜品。
     * <p>
     * @ Valid 触发 DishDTO 上的参数校验（菜名/分类/价格不能为空）。
     */
    @PostMapping
    public Result<Void> addDish(@Valid @RequestBody DishDTO dto) {
        dishService.addDish(dto);
        return Result.ok("添加成功！", null);
    }

    /**
     * 修改菜品（字段为 null 表示不修改）。
     */
    @PutMapping("/{dishId}")
    public Result<Void> editDish(@PathVariable("dishId") Long dishId, @Valid @RequestBody UpdateDishDTO dto) {
        dishService.modifyDish(dishId, dto);
        return Result.ok("修改成功！", null);
    }

    /**
     * 删除菜品。
     */
    @DeleteMapping("/{dishId}")
    public Result<Void> deleteDish(@PathVariable("dishId") Long dishId) {
        dishService.deleteDish(dishId);
        return Result.ok("删除成功！", null);
    }

}
