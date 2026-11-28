package com.example.onlineorder.pojo.DTO.admin;

import com.example.onlineorder.common.constant.DishStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改菜品请求体（DTO）。
 * <p>
 * 修改场景专用：字段为 null 表示「不修改该字段」，
 * 配合 updateById 只更新非 null 字段，实现部分更新。
 */
@Data
public class UpdateDishDTO {

    /** 菜品名称（null = 不修改） */
    private String name;

    /** 分类（null = 不修改） */
    private String category;

    /** 价格（null = 不修改） */
    private BigDecimal price;

    /** 库存（null = 不修改） */
    private Integer stock;

    /** 状态：0 下架 / 1 上架（null = 不修改） */
    private DishStatusEnum status;
}
