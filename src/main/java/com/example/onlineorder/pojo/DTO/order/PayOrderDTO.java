package com.example.onlineorder.pojo.DTO.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付订单请求体（DTO），对应接口 7：POST /api/orders/{id}/pay。
 */
@Data
public class PayOrderDTO {

    /** 下单顾客编号（临时字段，顾客登录实现后改为从 token 取） */
    @Schema(example = "1")
    @NotNull(message = "顾客编号不能为空")
    private Long customerId;

    /** 支付方式：1 微信，2 支付宝，3 其他 */
    @Schema(example = "1")
    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;
}
