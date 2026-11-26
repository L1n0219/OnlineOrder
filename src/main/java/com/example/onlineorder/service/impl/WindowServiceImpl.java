package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.EmployeeMapper;
import com.example.onlineorder.mapper.PickupWindowMapper;
import com.example.onlineorder.pojo.DTO.admin.NewWindowDTO;
import com.example.onlineorder.pojo.entity.Employee;
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
}
