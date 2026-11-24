package com.example.onlineorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.WindowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/windows")
public class AdminWindowController {
    @Resource
    private WindowService windowService;

    @GetMapping
    public Result<PageResult<PickupWindow>> getWindows(@RequestParam(defaultValue = "1") long page,
                                                       @RequestParam(defaultValue = "10") long size) {
        Page<PickupWindow> result = windowService.listWindows(page, size);
        return Result.ok("成功获取取餐窗口列表",new PageResult<>(result.getTotal(),result.getRecords()));
    }

    @GetMapping("/{windowId}")
    public Result<Employee> getEmployee(@PathVariable("windowId") long windowId) {
        Employee result = windowService.getEmployeeByWindowId(windowId);
        return Result.ok("已查找到窗口 "+ windowId +" 的负责员工，编号："+result.getEmployeeId(),result);
    }
}
