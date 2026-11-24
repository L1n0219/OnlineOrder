package com.example.onlineorder.pojo.DTO.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 员工登录请求体（DTO）。
 * <p>
 * 前端登录时提交的 JSON：{ "username": "...", "password": "..." }。
 * 两个字段都加了 @NotBlank 校验，缺了会被全局异常处理器接住返回 400。
 */
@Data
public class LoginDTO {

    /** 登录账号 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文，后端用 BCrypt 和库里哈希比对） */
    @NotBlank(message = "密码不能为空")
    private String password;
}
