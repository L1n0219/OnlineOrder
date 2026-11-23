package com.example.onlineorder.pojo.VO.admin;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 营业统计视图对象（VO）。
 * <p>
 * 作为「管理员查看营业统计」接口的返回结果，把散落在订单表里的数据
 * 聚合成本次统计要展示的三个指标：营业额、订单总数、已完成订单数。
 * 完成率不在这里存字段，而是由 Service 层用 finishedCount / orderCount
 * 现算得到，避免维护一个容易不一致的冗余字段。
 */

@Data
public class StatisticsVO {
    private BigDecimal totalRevenue;   // 营业额（已支付订单实付金额之和）
    private Long orderCount;           // 订单总数
    private Long finishedCount;        // 已完成订单数
    // 完成率 = finishedCount / orderCount，在 Service 里计算，不存字段
}
