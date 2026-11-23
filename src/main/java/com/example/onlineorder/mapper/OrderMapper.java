package com.example.onlineorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineorder.pojo.VO.admin.StatisticsVO;
import com.example.onlineorder.pojo.entity.Order;
import org.apache.ibatis.annotations.Select;

/**
 * 订单表（orders）的 Mapper 接口。
 * <p>
 * 继承 {@link BaseMapper}<{@link Order}> 后自动获得订单表的通用增删改查能力；
 * 这里再定义需要自己写 SQL 的查询（目前是营业统计）。
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 营业统计：汇总营业额（已支付订单实付金额之和）、订单总数、已完成订单数。
     * <p>
     * 营业额只统计已支付（paid_at 非空）的订单；已完成订单以 status = 3 计。
     * SQL语句含义：
     * 查 orders 表，算三个数：
     * ① 已支付订单的实付金额总和（营业额）；
     * ② 订单总数；③ 已完成订单数。
     * 把这三个数分别命名为 totalRevenue、orderCount、finishedCount，装进 StatisticsVO 返回。
     */
    @Select("SELECT IFNULL(SUM(CASE WHEN paid_at IS NOT NULL THEN payable_amount ELSE 0 END), 0) AS totalRevenue, " +
            "COUNT(*) AS orderCount, " +
            "COUNT(CASE WHEN status = 3 THEN 1 END) AS finishedCount " +
            "FROM orders")
    StatisticsVO statistics();
}
