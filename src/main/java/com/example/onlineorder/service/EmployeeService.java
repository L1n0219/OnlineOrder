package com.example.onlineorder.service;

import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.pojo.DTO.auth.LoginDTO;
import com.example.onlineorder.pojo.VO.auth.LoginVO;

/**
 * 员工 Service 接口。
 */
public interface EmployeeService {

    /**
     * 员工登录：校验用户名密码，成功后生成 token。
     *
     * @param dto 登录请求体（用户名 + 密码）
     * @return 登录结果（token + 员工信息）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 新增员工（也可用于初始化管理员：role 传 2）。
     *
     * @param dto 新增员工的请求体
     */
    void register(RegisterDTO dto);
}
