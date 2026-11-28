package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.OrderStatusEnum;
import com.example.onlineorder.pojo.DTO.order.CancelOrderDTO;
import com.example.onlineorder.pojo.DTO.order.CreateOrderDTO;
import com.example.onlineorder.pojo.DTO.order.PayOrderDTO;
import com.example.onlineorder.pojo.VO.order.OrderVO;
import com.example.onlineorder.pojo.entity.Order;

import java.util.List;

/**
 * 订单 Service 接口。
 * <p>
 * 同时服务于员工端（13 号：本窗口订单列表）和顾客端（6/7/8：下单、支付、取消）。
 */
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

    /**
     * 下单（6 号接口）。
     * <p>
     * 校验顾客、窗口、菜品与库存，按规则计算折扣，扣减库存后写入订单与明细。
     *
     * @param dto 下单请求体
     * @return 新建的订单信息
     */
    OrderVO createOrder(CreateOrderDTO dto);

    /**
     * 支付订单（7 号接口）。
     *
     * @param orderId 订单编号
     * @param dto     支付请求体（顾客编号 + 支付方式）
     * @return 支付后的订单信息
     */
    OrderVO payOrder(Long orderId, PayOrderDTO dto);

    /**
     * 取消订单（8 号接口）。
     * <p>
     * 只有「待接单」的订单可以取消，取消后回补库存。
     *
     * @param orderId 订单编号
     * @param dto     取消请求体（顾客编号）
     * @return 取消后的订单信息
     */
    OrderVO cancelOrder(Long orderId, CancelOrderDTO dto);
}
