package com.example.onlineorder.pojo.VO.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细的返回结构（VO）。
 */
@Data
public class OrderItemVO {

    /** 菜品编号 */
    private Long dishId;

    /** 菜品名称 */
    private String dishName;

    /** 数量 */
    private Integer quantity;

    /** 下单时的单价（快照） */
    private BigDecimal unitPrice;

    /** 小计 = 单价 × 数量 */
    private BigDecimal subtotal;
}
