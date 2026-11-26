package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.EmployeeMapper;
import com.example.onlineorder.mapper.OrderMapper;
import com.example.onlineorder.mapper.PickupWindowMapper;
import com.example.onlineorder.pojo.DTO.admin.NewWindowDTO;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.Order;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.WindowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 取餐窗口 Service 实现。
 */
@Service
public class WindowServiceImpl implements WindowService {

    @Resource
    private PickupWindowMapper windowMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private OrderMapper orderMapper;

    /**
     * 分页查询取餐窗口列表。
     */
    @Override
    public Page<PickupWindow> listWindows(long page, long size) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);

        return windowMapper.selectPage(new Page<>(page, size), null);
    }

    /**
     * 根据窗口编号查询负责该窗口的员工。
     * <p>
     * 先查窗口拿到 employee_id，再查员工表返回员工信息。
     */
    @Override
    public Employee getEmployeeByWindowId(Long windowId) {
        if (windowId == null) {
            throw new BusinessException("窗口编号不能为空");
        }
        PickupWindow window = windowMapper.selectById(windowId);
        if (window == null) {
            throw new BusinessException("未找到编号为 " + windowId + " 的取餐窗口");
        }
        Long empId = window.getEmployeeId();
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) {
            throw new BusinessException("未找到编号为 " + empId + " 的员工");
        }
        return emp;
    }

    /**
     * 新增取餐窗口。
     * <p>
     * window_id 是自增主键，由数据库自动生成，这里只设置负责员工编号即可。
     */
    @Override
    public void addWindow(NewWindowDTO dto) {
        PickupWindow window = new PickupWindow();
        window.setEmployeeId(dto.getEmpId());
        windowMapper.insert(window);
    }

    /**
     * 修改窗口的负责员工。
     * <p>
     * 先查窗口确认存在，再更新 employee_id 字段。
     */
    @Override
    public void modifyWindow(Long windowId, Long empId) {
        if (windowId == null) {
            throw new BusinessException("修改的窗口编号不能为空");
        }
        if (empId == null) {
            throw new BusinessException("负责员工编号不能为空");
        }
        PickupWindow window = windowMapper.selectById(windowId);
        if (window == null) {
            throw new BusinessException("未找到编号为 " + windowId + " 的窗口");
        }
        window.setEmployeeId(empId);
        windowMapper.updateById(window);
    }

    /**
     * 删除取餐窗口。
     * <p>
     * 删除前先检查 orders 表里是否还有引用该窗口的订单，
     * 有则禁止删除，避免破坏外键约束、出现"悬空订单"。
     */
    @Override
    public void deleteWindow(Long windowId) {
        if (windowId == null) {
            throw new BusinessException("删除的窗口编号不能为空");
        }
        // 检查该窗口是否还有订单（不管订单状态，只要被引用就不能删）
        Long orderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getWindowId, windowId));
        if (orderCount != null && orderCount > 0) {
            throw new BusinessException("该窗口存在订单，无法删除");
        }
        windowMapper.deleteById(windowId);
    }
}
