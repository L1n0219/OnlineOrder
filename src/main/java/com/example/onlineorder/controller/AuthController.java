package com.example.onlineorder.controller;

import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.auth.LoginDTO;
import com.example.onlineorder.pojo.VO.auth.LoginVO;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口，统一前缀 /api/auth。
 * <p>
 * 注意：/api/auth/** 已在 SaTokenConfigure 里放行，登录接口不需要带 token。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录。
     * <p>
     * 请求体：{ "username": "...", "password": "..." }。
     * 成功后返回 token，前端把它放进后续请求的 Sa-token 请求头里。
     */
    @PostMapping("/employee/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok("登录成功", employeeService.login(dto));
    }
}
