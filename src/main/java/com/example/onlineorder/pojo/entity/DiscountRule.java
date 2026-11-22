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
 * 这里是discount_rule表对应的实体类DiscountRule
 * 已经完成了与表的匹配，属性含义请查字段文档
 */

//@Data注解自动为类生成getter/setter方法
@Data
//@TableName注解让其修饰的类绑定到数据库的表上
@TableName("`discount_rule`")
//@NoArgsConstructor自动生成无参构造函数
@NoArgsConstructor
//@AllArgsConstructor自动生成有参构造函数
@AllArgsConstructor
public class DiscountRule {
    @TableId(value = "rule_id",type = IdType.AUTO)
    private Long ruleId;

    @TableField(value = "rule_name")
    private String ruleName;

    @TableField(value = "threshold")
    private BigDecimal threshold;

    @TableField(value = "discount_rate")
    private BigDecimal discountRate;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "created_at")
    private LocalDateTime createTime;

    @TableField(value = "valid_from")
    private LocalDateTime startTime;

    @TableField(value = "valid_to")
    private LocalDateTime endTime;

}
