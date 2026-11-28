package com.example.onlineorder.pojo.DTO.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 下单请求体（DTO），对应接口 6：POST /api/orders。
 * <p>
 * 说明：顾客登录接口（2 号）还没实现，所以目前由调用方在请求体里传 customerId 标识顾客；
 * 等顾客登录接口做好后，应改成从 token 里取 customerId，这里的字段就可以删掉。
 */
@Data
public class CreateOrderDTO {

    /** 下单顾客编号（临时字段，见类注释） */
    @Schema(example = "1")
    @NotNull(message = "顾客编号不能为空")
    private Long customerId;

    /** 取餐窗口编号 */
    @Schema(example = "1")
    @NotNull(message = "取餐窗口不能为空")
    private Long windowId;

    /** 桌号，打包带走可以不传 */
    @Schema(example = "A03")
    @Size(max = 20, message = "桌号长度不能超过 20 个字符")
    private String tableNo;

    /** 菜品明细，至少一项 */
    @Valid
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemDTO> items;

    /** 可选：指定使用的折扣规则；不传或传 0 则自动匹配当前最优规则 */
    @Schema(example = "0", description = "可选，不传或传 0 表示自动匹配最优折扣规则")
    private Long discountRuleId;
}
