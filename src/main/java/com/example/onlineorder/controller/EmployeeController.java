package com.example.onlineorder.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.common.constant.OrderStatusEnum;
import com.example.onlineorder.pojo.entity.Order;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.EmployeeService;
import com.example.onlineorder.service.OrderService;
import com.example.onlineorder.service.WindowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Resource
    private OrderService orderService;

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

    /**
     * 分页查询当前员工负责窗口的订单，可按状态筛选。
     * <p>
     * 员工编号从 token 解析，再查其负责的窗口，按窗口编号列表查订单；
     * status 可选，不传则查全部状态。
     */
    @GetMapping("/orders")
    public Result<PageResult<Order>> listOrdersWithStatus(@RequestParam(defaultValue = "1") long page,
                                                          @RequestParam(defaultValue = "10") long size,
                                                          @RequestParam(required = false) OrderStatusEnum status) {
        Long empId = StpUtil.getLoginIdAsLong();
        // 查员工负责的窗口，提取窗口编号列表
        List<PickupWindow> windows = employeeService.getPickupWindow(empId);
        List<Long> windowIds = windows.stream()
                .map(PickupWindow::getWindowId).toList();
        Page<Order> result = orderService.listOrdersWithStatus(page, size, status, windowIds);
        return Result.ok("成功获取订单列表", new PageResult<>(result.getTotal(), result.getRecords()));
    }
}
