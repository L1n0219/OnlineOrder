package com.example.onlineorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author LIN
 * 这里是customer表对应的实体类Customer
 * 已经完成了与表的匹配，属性含义请查字段文档
 */
//@Data注解自动为类生成getter/setter方法
@Data
//@TableName注解让其修饰的类绑定到数据库的表上
@TableName("`customer`")
//@NoArgsConstructor自动生成无参构造函数
@NoArgsConstructor
//@AllArgsConstructor自动生成有参构造函数
@AllArgsConstructor
public class Customer {
    @TableId(value = "customer_id",type = IdType.AUTO)
    private Long customerId;

    @TableField(value = "nickname")
    private String name;

    @TableField(value = "phone")
    private String phoneNum;

    @TableField(value = "wechat_openid")
    private String wechatOpenId;

    @TableField(value = "created_at")
    private LocalDateTime createTime;
}
