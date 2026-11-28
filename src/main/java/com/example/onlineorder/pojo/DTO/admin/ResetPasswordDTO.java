package com.example.onlineorder.pojo.DTO.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置员工密码请求体（DTO）。
 * <p>
 * 管理员为指定员工设置新密码，后端用 BCrypt 加密后写入 password_hash 列。
 */
@Data
public class ResetPasswordDTO {

    /** 新密码（明文传输，后端 BCrypt 加密入库，绝不存明文） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需在 6~64 位之间")
    private String newPassword;
}