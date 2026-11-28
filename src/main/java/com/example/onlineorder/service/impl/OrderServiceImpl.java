package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.OrderStatusEnum;
import com.example.onlineorder.mapper.OrderMapper;
import com.example.onlineorder.pojo.entity.Order;
import com.example.onlineorder.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    /**
     * 分页查询指定窗口列表的订单，可按状态筛选。
     * <p>
     * status 为 null 时不筛状态（查全部）；窗口列表为空时直接返回空页。
     */
    @Override
    public Page<Order> listOrdersWithStatus(long page, long size, OrderStatusEnum status, List<Long> windowIds) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);

        // 员工没负责任何窗口，直接返回空页，避免生成 "IN ()" 语法错误
        if (windowIds == null || windowIds.isEmpty()) {
            return new Page<>(page, size);
        }

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>();
        queryWrapper.eq(status != null, Order::getStatus, status)
                .in(Order::getWindowId, windowIds);

        return orderMapper.selectPage(new Page<>(page, size), queryWrapper);
    }
}
