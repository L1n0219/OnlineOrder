package com.example.onlineorder.controller;

import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员-员工管理接口，统一前缀 /api/admin/employees。
 * <p>
 * 该路径下的接口都会被 Sa-Token 拦截器要求「登录 + admin 角色」，
 * 所以只有管理员能新增员工/管理员。
 */
@RestController
@RequestMapping("/api/admin/employees")
public class AdminEmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增员工（或管理员，role 传 2）。
     * <p>
     * 请求体：{ "username": "...", "password": "...", "name": "...", "role": 2, "idCardNum": "..." }。
     * 密码在后端用 BCrypt 加密后入库。
     */
    @PostMapping
    public Result<Void> addEmployee(@Valid @RequestBody RegisterDTO dto) {
        employeeService.register(dto);
        return Result.ok("新增员工成功", null);
    }
}
