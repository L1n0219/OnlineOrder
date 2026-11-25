package com.example.onlineorder.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 员工端接口，统一前缀 /api/employee。
 * <p>
 * 面向「已登录员工」，这些接口会被 Sa-Token 拦截器要求登录（不带 token 会返回 401）。
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 获取当前登录员工负责的取餐窗口。
     * <p>
     * 员工编号不从请求参数取，而是用 StpUtil.getLoginIdAsLong() 从 token 里解析，
     * 防止员工伪造别人的 id 去查别人的窗口（越权）。
     */
    @GetMapping("/windows")
    public Result<List<PickupWindow>> getMyWindow() {
        Long empId = StpUtil.getLoginIdAsLong();
        List<PickupWindow> windows = employeeService.getPickupWindow(empId);
        return Result.ok("已找到负责窗口", windows);
    }
}
