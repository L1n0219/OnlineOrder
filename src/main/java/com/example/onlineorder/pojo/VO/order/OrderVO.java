package com.example.onlineorder.pojo.VO.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单的返回结构（VO）。
 * <p>
 * 只暴露前端需要的字段，不直接返回数据库实体。
 */
@Data
public class OrderVO {

    /** 订单编号 */
    private Long orderId;

    /** 顾客编号 */
    private Long customerId;

    /** 取餐窗口编号 */
    private Long windowId;

    /** 桌号（打包为空） */
    private String tableNo;

    /** 状态码：0 待接单 1 备餐中 2 已出餐 3 已完成 4 已取消 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 原价总额 */
    private BigDecimal totalAmount;

    /** 实付金额（折扣后） */
    private BigDecimal payableAmount;

    /** 使用的折扣规则编号 */
    private Long discountRuleId;

    /** 使用的折扣规则名称 */
    private String discountRuleName;

    /** 支付方式：null 未支付，1 微信 2 支付宝 3 其他 */
    private Integer payMethod;

    /** 支付方式描述 */
    private String payMethodDesc;

    /** 支付时间 */
    private LocalDateTime paidTime;

    /** 下单时间 */
    private LocalDateTime createTime;

    /** 订单明细 */
    private List<OrderItemVO> items;
}
