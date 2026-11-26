package com.example.onlineorder.pojo.DTO.admin;

import com.example.onlineorder.common.constant.DiscountStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountRuleDTO {
    @NotBlank(message = "规则明细不能为空")
    private String ruleName;

    @NotNull(message = "满额阈值不能为空")
    private BigDecimal threshold;

    @NotNull(message = "折扣率不能为空")
    private BigDecimal discountRate;

    private DiscountStatusEnum status = DiscountStatusEnum.ENABLED;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
