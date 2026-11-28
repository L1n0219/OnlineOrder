package com.example.onlineorder.pojo.DTO.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 下单时的单个菜品明细。
 * <p>
 * 一个订单里同一个菜品只能出现一次（order_item 表有 UNIQUE(order_id, dish_id)），
 * Service 里会先把重复的 dishId 合并数量。
 */
@Data
public class OrderItemDTO {

    /** 菜品编号 */
    @Schema(example = "2")
    @NotNull(message = "菜品编号不能为空")
    private Long dishId;

    /** 数量 */
    @Schema(example = "1")
    @NotNull(message = "菜品数量不能为空")
    @Min(value = 1, message = "菜品数量至少为 1")
    @Max(value = 99, message = "单个菜品数量不能超过 99")
    private Integer quantity;
}
