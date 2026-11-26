package com.example.onlineorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.pojo.DTO.admin.DiscountRuleDTO;
import com.example.onlineorder.pojo.entity.DiscountRule;

/**
 * 折扣规则 Service 接口。
 */
public interface DiscountRuleService {

    /**
     * 分页查询折扣规则列表。
     *
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 折扣规则分页结果
     */
    Page<DiscountRule> listDiscountRules(long page, long size);

    /**
     * 新增折扣规则。
     *
     * @param dto 新增折扣规则请求体
     */
    void addDiscountRule(DiscountRuleDTO dto);

    /**
     * 修改折扣规则。
     *
     * @param ruleId 折扣规则编号
     * @param dto    修改后的折扣规则数据
     */
    void modifyDiscountRule(Long ruleId, DiscountRuleDTO dto);

    /**
     * 删除折扣规则。
     *
     * @param ruleId 折扣规则编号
     */
    void deleteDiscountRule(Long ruleId);
}
