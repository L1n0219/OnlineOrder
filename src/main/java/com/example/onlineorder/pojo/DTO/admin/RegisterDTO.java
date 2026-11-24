package com.example.onlineorder.pojo.DTO.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增员工请求体（DTO）。
 * <p>
 * 管理员通过「新增员工」接口创建员工/管理员账号时提交的数据。
 * 注意 password 是明文，后端会用 BCrypt 加密后存入 password_hash 列，绝不存明文。
 */
@Data
public class RegisterDTO {

    /** 登录账号（employee 表有唯一索引，不能重复） */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文，后端 BCrypt 加密后入库） */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 角色：1 员工，2 管理员（默认 1 员工） */
    private Integer role = 1;

    /** 性别：0 未知，1 男，2 女（默认 0 未知） */
    private Integer gender = 0;

    /** 出生日期（可空） */
    private LocalDate birthDate;

    /** 证件号（employee 表有唯一索引，不能重复） */
    @NotBlank(message = "证件号不能为空")
    private String idCardNum;
}
