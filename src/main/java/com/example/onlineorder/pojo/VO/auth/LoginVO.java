package com.example.onlineorder.pojo.VO.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功后的返回结果（VO）。
 * <p>
 * 返回 token 以及员工的基本信息，前端拿到 token 后，
 * 在后续请求的请求头里带上 satoken 字段即可通过鉴权。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** 登录令牌，前端后续放在 satoken 请求头里 */
    private String token;

    /** 员工编号 */
    private Long employeeId;

    /** 登录账号 */
    private String username;

    /** 姓名 */
    private String name;

    /** 角色：1 员工，2 管理员 */
    private Integer role;
}
