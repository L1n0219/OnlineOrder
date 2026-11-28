package com.example.onlineorder.pojo.DTO.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 修改员工信息请求体（DTO）。
 * <p>
 * 只提交需要修改的字段即可，没传（null）的字段保持原值不变。
 * 用户名创建后不可改；密码不在这里修改，请走「重置员工密码」接口。
 * <p>
 * 注意：本类是接口的请求体（DTO），不是数据库实体，不参与 MyBatis-Plus 的表字段映射，
 * 因此不需要 @TableField；字段与列的对应关系定义在实体 Employee 上（idCardNum 已标注 @TableField("id_card")）。
 */
@Data
public class UpdateEmployeeDTO {

    /** 姓名（不传则不改） */
    @Schema(example = "刘洋")
    @Size(max = 50, message = "姓名长度不能超过 50 个字符")
    private String name;

    /** 角色：1 员工，2 管理员（不传则不改；系统只允许存在一个管理员账号） */
    @Schema(example = "1")
    private Integer role;

    /** 性别：0 未知，1 男，2 女（不传则不改） */
    @Schema(example = "1")
    private Integer gender;

    /** 出生日期（不传则不改） */
    @Schema(example = "1995-05-17")
    private LocalDate birthDate;

    /** 证件号（employee 表有唯一索引，不传则不改） */
    @Schema(example = "110101199505170056")
    @Size(max = 18, message = "证件号长度不能超过 18 个字符")
    private String idCardNum;
}