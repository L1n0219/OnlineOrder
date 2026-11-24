package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;

public interface WindowService {

    Page<PickupWindow> listWindows(long page, long size);

    Employee getEmployeeByWindowId(Long tabId);
}
