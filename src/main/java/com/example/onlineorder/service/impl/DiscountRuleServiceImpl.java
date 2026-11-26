package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.DiscountStatusEnum;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.DiscountRuleMapper;
import com.example.onlineorder.mapper.OrderMapper;
import com.example.onlineorder.pojo.DTO.admin.DiscountRuleDTO;
import com.example.onlineorder.pojo.entity.DiscountRule;
import com.example.onlineorder.pojo.entity.Order;
import com.example.onlineorder.service.DiscountRuleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 折扣规则 Service 实现。
 */
@Service
public class DiscountRuleServiceImpl implements DiscountRuleService {

    @Resource
    private DiscountRuleMapper discountRuleMapper;

    @Resource
    private OrderMapper orderMapper;

    /**
     * 分页查询折扣规则列表。
     */
    @Override
    public Page<DiscountRule> listDiscountRules(long page, long size) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);

        return discountRuleMapper.selectPage(new Page<>(page, size), null);
    }

    /**
     * 新增折扣规则。
     * <p>
     * 先按「满额阈值 + 折扣率」查重，避免插入重复规则，再组装实体入库。
     */
    @Override
    public void addDiscountRule(DiscountRuleDTO dto) {
        // 1. 查重：同一「阈值 + 折扣率」视为重复规则
        Long count = discountRuleMapper.selectCount(
                new LambdaQueryWrapper<DiscountRule>()
                        .eq(DiscountRule::getDiscountRate, dto.getDiscountRate())
                        .eq(DiscountRule::getThreshold, dto.getThreshold()));
        if (count != null && count > 0) {
            throw new BusinessException("已存在重复的折扣");
        }

        // 2. 组装实体入库
        DiscountRule discountRule = new DiscountRule();
        discountRule.setRuleName(dto.getRuleName());
        discountRule.setThreshold(dto.getThreshold());
        discountRule.setDiscountRate(dto.getDiscountRate());
        discountRule.setStatus(dto.getStatus());
        discountRule.setStartTime(dto.getStartTime());
        discountRule.setEndTime(dto.getEndTime());
        discountRuleMapper.insert(discountRule);
    }

    /**
     * 修改折扣规则。
     * <p>
     * 用 LambdaUpdateWrapper 显式 set 每个字段（而不是 updateById），
     * 这样起止时间即使传 null 也会被更新成 null（清空有效期），
     * 而 updateById 默认会跳过 null 字段，无法清空。
     */
    @Override
    public void modifyDiscountRule(Long ruleId, DiscountRuleDTO dto) {
        if (ruleId == null) {
            throw new BusinessException("折扣规则编号不能为空");
        }
        // 先确认规则存在，给出友好提示
        if (discountRuleMapper.selectById(ruleId) == null) {
            throw new BusinessException("折扣规则不存在！");
        }

        // 显式 set 每个字段，null 值也会生成 SET 语句，实现真正的"覆盖式"更新
        discountRuleMapper.update(null, new LambdaUpdateWrapper<DiscountRule>()
                .eq(DiscountRule::getRuleId, ruleId)
                .set(DiscountRule::getRuleName, dto.getRuleName())
                .set(DiscountRule::getThreshold, dto.getThreshold())
                .set(DiscountRule::getDiscountRate, dto.getDiscountRate())
                .set(DiscountRule::getStatus, dto.getStatus())
                .set(DiscountRule::getStartTime, dto.getStartTime())
                .set(DiscountRule::getEndTime, dto.getEndTime()));
    }

    /**
     * 删除折扣规则。
     * <p>
     * 删除前检查规则是否「启用中」或「已被订单引用」，
     * 两种情况都不允许删除，避免误删生效中的规则、破坏外键约束。
     */
    @Override
    public void deleteDiscountRule(Long ruleId) {
        if (ruleId == null) {
            throw new BusinessException("折扣规则编号不能为空");
        }
        DiscountRule discountRule = discountRuleMapper.selectById(ruleId);
        if (discountRule == null) {
            throw new BusinessException("折扣规则不存在！");
        }
        // 检查是否被订单引用，有则禁止删除
        Long orderCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getDiscountRuleId, ruleId));
        if (DiscountStatusEnum.ENABLED.equals(discountRule.getStatus()) || (orderCount != null && orderCount > 0)) {
            throw new BusinessException("该折扣规则正在被占用！");
        }
        discountRuleMapper.deleteById(ruleId);
    }
}
