package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.pojo.DTO.admin.NewWindowDTO;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;

/**
 * 取餐窗口 Service 接口。
 */
public interface WindowService {

    /**
     * 分页查询取餐窗口列表。
     *
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 窗口分页结果
     */
    Page<PickupWindow> listWindows(long page, long size);

    /**
     * 根据窗口编号查询负责该窗口的员工。
     *
     * @param windowId 窗口编号
     * @return 负责该窗口的员工
     */
    Employee getEmployeeByWindowId(Long windowId);

    /**
     * 新增取餐窗口。
     *
     * @param dto 新增窗口请求体（负责员工编号）
     */
    void addWindow(NewWindowDTO dto);
}
