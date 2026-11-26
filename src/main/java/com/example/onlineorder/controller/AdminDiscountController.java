package com.example.onlineorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.PageResult;
import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.admin.DiscountRuleDTO;
import com.example.onlineorder.pojo.entity.DiscountRule;
import com.example.onlineorder.service.DiscountRuleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-折扣规则管理接口，统一前缀 /api/admin/discount-rules。
 * <p>
 * 该路径下的接口会被 Sa-Token 拦截器要求「登录 + admin 角色」。
 */
@RestController
@RequestMapping("/api/admin/discount-rules")
public class AdminDiscountController {

    @Resource
    private DiscountRuleService discountRuleService;

    /**
     * 分页查询折扣规则列表。
     */
    @GetMapping
    public Result<PageResult<DiscountRule>> getAllDiscountRules(@RequestParam(defaultValue = "1") long page,
                                                                @RequestParam(defaultValue = "10") long size) {
        Page<DiscountRule> result = discountRuleService.listDiscountRules(page, size);
        return Result.ok("成功获取折扣规则列表", new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 新增折扣规则。
     */
    @PostMapping
    public Result<Void> addDiscountRule(@Valid @RequestBody DiscountRuleDTO dto) {
        discountRuleService.addDiscountRule(dto);
        return Result.ok("添加折扣规则完毕", null);
    }

    /**
     * 修改折扣规则。
     */
    @PutMapping("/{ruleId}")
    public Result<Void> editDiscountRule(@PathVariable("ruleId") Long ruleId,
                                         @Valid @RequestBody DiscountRuleDTO dto) {
        discountRuleService.modifyDiscountRule(ruleId, dto);
        return Result.ok("修改完毕", null);
    }

    /**
     * 删除折扣规则。
     */
    @DeleteMapping("/{ruleId}")
    public Result<Void> deleteDiscountRule(@PathVariable("ruleId") Long ruleId) {
        discountRuleService.deleteDiscountRule(ruleId);
        return Result.ok("删除完毕", null);
    }
}
