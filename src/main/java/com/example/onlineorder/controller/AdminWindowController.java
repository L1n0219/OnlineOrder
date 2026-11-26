package com.example.onlineorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.admin.NewWindowDTO;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.WindowService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-取餐窗口管理接口，统一前缀 /api/admin/windows。
 * <p>
 * 该路径下的接口会被 Sa-Token 拦截器要求「登录 + admin 角色」。
 */
@RestController
@RequestMapping("/api/admin/windows")
public class AdminWindowController {

    @Resource
    private WindowService windowService;

    /**
     * 分页查询取餐窗口列表。
     */
    @GetMapping
    public Result<PageResult<PickupWindow>> getWindows(@RequestParam(defaultValue = "1") long page,
                                                       @RequestParam(defaultValue = "10") long size) {
        Page<PickupWindow> result = windowService.listWindows(page, size);
        return Result.ok("成功获取取餐窗口列表", new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 查询指定窗口的负责员工。
     */
    @GetMapping("/{windowId}")
    public Result<Employee> getEmployee(@PathVariable("windowId") long windowId) {
        Employee result = windowService.getEmployeeByWindowId(windowId);
        return Result.ok("已查找到窗口 " + windowId + " 的负责员工", result);
    }

    /**
     * 新增取餐窗口。
     * <p>
     * 请求体只传负责员工编号即可，窗口编号由数据库自增生成。
     */
    @PostMapping
    public Result<Void> newWindow(@Valid @RequestBody NewWindowDTO dto) {
        windowService.addWindow(dto);
        return Result.ok("添加新窗口成功", null);
    }
}
