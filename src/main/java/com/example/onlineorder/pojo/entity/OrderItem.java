package com.example.onlineorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author LIN
 * 这里是order_item表对应的实体类OrderItem
 * 已经完成了与表的匹配，属性含义请查字段文档
 */

//@Data注解自动为类生成getter/setter方法
@Data
//@TableName注解让其修饰的类绑定到数据库的表上
@TableName("`order_item`")
//@NoArgsConstructor自动生成无参构造函数
@NoArgsConstructor
//@AllArgsConstructor自动生成有参构造函数
@AllArgsConstructor
public class OrderItem {
    @TableId(value = "item_id",type = IdType.AUTO)
    private Long itemId;

    @TableField(value = "order_id")
    private Long orderId;

    @TableField(value = "dish_id")
    private Long dishId;

    @TableField(value = "quantity")
    private Integer quantity;

    @TableField(value = "unit_price_snapshot")
    private BigDecimal priceSnapshot;
}
