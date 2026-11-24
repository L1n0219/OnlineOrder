package com.example.onlineorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.onlineorder.common.constant.DishStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author LIN
 * 这里是 dish 表对应的实体类 Dish
 * 已经完成了与表的匹配，属性含义请查字段文档
 */
@Data
@TableName("`dish`")
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    /** 菜品编号（主键，自增） */
    @TableId(value = "dish_id", type = IdType.AUTO)
    private Long dishId;

    /** 菜品名称 */
    @TableField(value = "name")
    private String name;

    /** 类别 */
    @TableField(value = "category")
    private String category;

    /** 价格（元） */
    @TableField(value = "price")
    private BigDecimal price;

    /** 剩余库存数 */
    @TableField(value = "stock")
    private Integer stock;

    /** 状态：0 下架，1 上架 */
    @TableField(value = "status")
    private DishStatusEnum status;
}
