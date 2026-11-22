package com.example.onlineorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author LIN
 * 这里是orders表对应的实体类Order
 * 已经完成了与表的匹配，属性含义请查字段文档
 */

//@Data注解自动为类生成getter/setter方法
@Data
//@TableName注解让其修饰的类绑定到数据库的表上
@TableName("`orders`")
//@NoArgsConstructor自动生成无参构造函数
@NoArgsConstructor
//@AllArgsConstructor自动生成有参构造函数
@AllArgsConstructor
public class Order {
    @TableId(value = "order_id",type = IdType.AUTO)
    private Long orderId;

    @TableField(value = "customer_id")
    private Long customerId;

    @TableField(value = "window_id")
    private Long windowId;

    @TableField(value = "table_no")
    private String tableNo;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private LocalDateTime createTime;

    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @TableField(value = "payable_amount")
    private BigDecimal payableAmount;

    @TableField(value = "discount_rule_id")
    private Long discountRuleId;

    @TableField(value = "paid_at")
    private LocalDateTime paidTime;

    @TableField(value = "pay_method")
    private Integer payMethod;
}
