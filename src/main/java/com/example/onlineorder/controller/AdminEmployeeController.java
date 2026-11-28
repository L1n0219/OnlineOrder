package com.example.onlineorder.controller;

import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.pojo.DTO.admin.ResetPasswordDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateEmployeeDTO;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员-员工管理接口，统一前缀 /api/admin/employees。
 * <p>
 * 该路径下的接口都会被 Sa-Token 拦截器要求「登录 + admin 角色」，
 * 所以只有管理员能新增/修改员工、重置密码。
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

    /**
     * 修改员工信息（20 号接口）。
     * <p>
     * 请求体只需传要改的字段，例如：{ "name": "张三", "gender": 1 }；
     * 没传的字段保持原值。用户名不支持修改，密码请用下面的重置接口。
     */
    @PutMapping("/{employeeId}")
    public Result<Void> updateEmployee(@PathVariable("employeeId") Long employeeId,
                                       @Valid @RequestBody UpdateEmployeeDTO dto) {
        employeeService.updateEmployee(employeeId, dto);
        return Result.ok("修改员工信息成功", null);
    }

    /**
     * 重置员工密码（21 号接口）。
     * <p>
     * 请求体：{ "newPassword": "123456" }，后端 BCrypt 加密后写回 password_hash。
     */
    @PutMapping("/{employeeId}/password")
    public Result<Void> resetPassword(@PathVariable("employeeId") Long employeeId,
                                      @Valid @RequestBody ResetPasswordDTO dto) {
        employeeService.resetPassword(employeeId, dto.getNewPassword());
        return Result.ok("重置密码成功", null);
    }
}