package com.example.onlineorder.controller;

import com.example.onlineorder.common.Result;
import com.example.onlineorder.pojo.DTO.order.CancelOrderDTO;
import com.example.onlineorder.pojo.DTO.order.CreateOrderDTO;
import com.example.onlineorder.pojo.DTO.order.PayOrderDTO;
import com.example.onlineorder.pojo.VO.order.OrderVO;
import com.example.onlineorder.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顾客端订单接口，统一前缀 /api/orders。
 * <p>
 * 覆盖接口清单里的 6（下单）、7（支付）、8（取消）。
 * 注意：顾客微信登录（2 号接口）尚未实现，所以当前由请求体里的 customerId 标识顾客；
 * 等顾客登录做好后，应改为从 Sa-Token 的 loginId 中取，并加上「顾客」角色校验。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 下单（6 号接口）。
     * <p>
     * 请求体示例：
     * { "customerId": 1, "windowId": 1, "tableNo": "A01",
     *   "items": [ { "dishId": 1, "quantity": 2 }, { "dishId": 3, "quantity": 1 } ] }
     * <p>
     * 不传 discountRuleId 时，后端自动匹配当前可用的最优折扣规则。
     */
    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        return Result.ok("下单成功", orderService.createOrder(dto));
    }

    /**
     * 支付订单（7 号接口）。
     * <p>
     * 请求体：{ "customerId": 1, "payMethod": 1 }，payMethod：1 微信 / 2 支付宝 / 3 其他。
     */
    @PostMapping("/{orderId}/pay")
    public Result<OrderVO> payOrder(@PathVariable("orderId") Long orderId,
                                    @Valid @RequestBody PayOrderDTO dto) {
        return Result.ok("支付成功", orderService.payOrder(orderId, dto));
    }

    /**
     * 取消订单（8 号接口）。
     * <p>
     * 请求体：{ "customerId": 1 }。只有「待接单」的订单能取消，取消后会回补库存。
     */
    @PostMapping("/{orderId}/cancel")
    public Result<OrderVO> cancelOrder(@PathVariable("orderId") Long orderId,
                                       @Valid @RequestBody CancelOrderDTO dto) {
        return Result.ok("取消成功", orderService.cancelOrder(orderId, dto));
    }
}
