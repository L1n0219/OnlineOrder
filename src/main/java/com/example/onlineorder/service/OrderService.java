package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.OrderStatusEnum;
import com.example.onlineorder.pojo.entity.Order;

import java.util.List;

public interface OrderService {

    /**
     * 分页查询指定窗口列表的订单，可按状态筛选。
     *
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @param status    订单状态（null 表示不筛选）
     * @param windowIds 窗口编号列表
     * @return 订单分页结果
     */
    Page<Order> listOrdersWithStatus(long page, long size, OrderStatusEnum status, List<Long> windowIds);
}
