package com.example.onlineorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author LIN
 * 这里是employee表对应的实体类Employee
 * 已经完成了与表的匹配，属性含义请查字段文档
 */

//@Data注解自动为类生成getter/setter方法
@Data
//@TableName注解让其修饰的类绑定到数据库的表上
@TableName("`employee`")
//@NoArgsConstructor自动生成无参构造函数
@NoArgsConstructor
//@AllArgsConstructor自动生成有参构造函数
@AllArgsConstructor
public class Employee {
    @TableId(value = "employee_id",type = IdType.AUTO)
    private Long employeeId;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password_hash")
    private String passwordHash;

    @TableField(value = "role")
    private Integer role;

    @TableField(value = "name")
    private String name;

    @TableField(value = "gender")
    private Integer gender;

    @TableField(value = "birth_date")
    private LocalDate birthDate;

    @TableField(value = "id_card")
    private String idCardNum;
}
