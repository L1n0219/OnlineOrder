package com.example.onlineorder.pojo.DTO.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单请求体（DTO），对应接口 8：POST /api/orders/{id}/cancel。
 */
@Data
public class CancelOrderDTO {

    /** 下单顾客编号（临时字段，顾客登录实现后改为从 token 取） */
    @Schema(example = "1")
    @NotNull(message = "顾客编号不能为空")
    private Long customerId;
}
