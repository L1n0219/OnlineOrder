package com.example.onlineorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineorder.common.constant.DiscountStatusEnum;
import com.example.onlineorder.common.constant.DishStatusEnum;
import com.example.onlineorder.common.constant.OrderStatusEnum;
import com.example.onlineorder.common.constant.PayMethodEnum;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.CustomerMapper;
import com.example.onlineorder.mapper.DiscountRuleMapper;
import com.example.onlineorder.mapper.DishMapper;
import com.example.onlineorder.mapper.OrderItemMapper;
import com.example.onlineorder.mapper.OrderMapper;
import com.example.onlineorder.mapper.PickupWindowMapper;
import com.example.onlineorder.pojo.DTO.order.CancelOrderDTO;
import com.example.onlineorder.pojo.DTO.order.CreateOrderDTO;
import com.example.onlineorder.pojo.DTO.order.OrderItemDTO;
import com.example.onlineorder.pojo.DTO.order.PayOrderDTO;
import com.example.onlineorder.pojo.VO.order.OrderItemVO;
import com.example.onlineorder.pojo.VO.order.OrderVO;
import com.example.onlineorder.pojo.entity.Customer;
import com.example.onlineorder.pojo.entity.DiscountRule;
import com.example.onlineorder.pojo.entity.Dish;
import com.example.onlineorder.pojo.entity.Order;
import com.example.onlineorder.pojo.entity.OrderItem;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 订单 Service 实现。
 * <p>
 * 包含员工端 13 号（本窗口订单列表）和顾客端 6/7/8（下单、支付、取消）。
 * 下单与取消都会改动库存，所以标注 @Transactional，任何一步失败整体回滚，
 * 避免出现「扣了库存却没生成订单」这类脏数据。
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private PickupWindowMapper pickupWindowMapper;
    @Resource
    private DiscountRuleMapper discountRuleMapper;

    /**
     * 分页查询指定窗口列表的订单，可按状态筛选（13 号接口）。
     * <p>
     * status 为 null 时不筛状态（查全部）；窗口列表为空时直接返回空页。
     */
    @Override
    public Page<Order> listOrdersWithStatus(long page, long size, OrderStatusEnum status, List<Long> windowIds) {
        // 分页参数兜底：页码最小 1，每页条数限制在 [1, 100] 之间
        page = Math.max(page, 1);
        size = size < 1 ? 10 : size;
        size = Math.min(size, 100);

        // 员工没负责任何窗口，直接返回空页，避免生成 "IN ()" 语法错误
        if (windowIds == null || windowIds.isEmpty()) {
            return new Page<>(page, size);
        }

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>();
        queryWrapper.eq(status != null, Order::getStatus, status)
                .in(Order::getWindowId, windowIds);

        return orderMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    /**
     * 下单（6 号接口）。
     * <p>
     * 流程：校验顾客/窗口 → 合并重复菜品 → 逐项校验菜品状态与库存并算原价 →
     * 匹配折扣规则算实付 → 扣库存（带条件，防超卖）→ 写主单与明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderDTO dto) {
        // 1. 顾客、窗口必须存在（orders 表对这两列有外键）
        Customer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在：" + dto.getCustomerId());
        }
        PickupWindow window = pickupWindowMapper.selectById(dto.getWindowId());
        if (window == null) {
            throw new BusinessException("取餐窗口不存在：" + dto.getWindowId());
        }

        // 2. 合并重复菜品：order_item 有 UNIQUE(order_id, dish_id)，同一菜品只能有一行
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (OrderItemDTO item : dto.getItems()) {
            merged.merge(item.getDishId(), item.getQuantity(), Integer::sum);
        }
        if (merged.isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }

        // 3. 逐项校验并累计原价
        List<Dish> dishes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            Dish dish = dishMapper.selectById(entry.getKey());
            if (dish == null) {
                throw new BusinessException("菜品不存在：" + entry.getKey());
            }
            if (dish.getStatus() != DishStatusEnum.ON_SALE) {
                throw new BusinessException("菜品已下架：" + dish.getName());
            }
            int quantity = entry.getValue();
            int stock = dish.getStock() == null ? 0 : dish.getStock();
            if (stock < quantity) {
                throw new BusinessException("库存不足：" + dish.getName() + "，剩余 " + stock + " 份");
            }
            total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(quantity)));
            dishes.add(dish);
        }
        total = total.setScale(2, RoundingMode.HALF_UP);

        // 4. 折扣：传了 ruleId 就用指定的，否则自动匹配当前最优规则
        DiscountRule rule = resolveDiscountRule(dto.getDiscountRuleId(), total);
        BigDecimal payable = total;
        if (rule != null) {
            payable = total.multiply(rule.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
        }

        // 5. 扣库存：WHERE stock >= 数量，影响行数为 0 说明被别人抢先买走了
        int index = 0;
        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            int rows = dishMapper.update(null, new LambdaUpdateWrapper<Dish>()
                    .setSql("stock = stock - " + entry.getValue())
                    .eq(Dish::getDishId, entry.getKey())
                    .ge(Dish::getStock, entry.getValue()));
            if (rows != 1) {
                throw new BusinessException("库存不足：" + dishes.get(index).getName());
            }
            index++;
        }

        // 6. 写主单：新订单状态是「待接单」，还未支付（pay_method / paid_at 为空）
        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setWindowId(dto.getWindowId());
        order.setTableNo(dto.getTableNo());
        order.setStatus(OrderStatusEnum.WAIT_FOR_COMMIT);
        order.setTotalAmount(total);
        order.setPayableAmount(payable);
        order.setDiscountRuleId(rule == null ? null : rule.getRuleId());
        orderMapper.insert(order);
        // 回查一次，取到数据库生成的 created_at 默认值（否则返回体里下单时间是 null）
        order = orderMapper.selectById(order.getOrderId());

        // 7. 写明细：单价用快照，之后菜品调价不影响历史订单
        List<OrderItemVO> itemVOs = new ArrayList<>();
        index = 0;
        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            Dish dish = dishes.get(index++);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setDishId(dish.getDishId());
            orderItem.setQuantity(entry.getValue());
            orderItem.setPriceSnapshot(dish.getPrice());
            orderItemMapper.insert(orderItem);
            itemVOs.add(toItemVO(dish, entry.getValue(), dish.getPrice()));
        }

        return toVO(order, rule, itemVOs);
    }

    /**
     * 支付订单（7 号接口）。
     * <p>
     * 只允许支付「自己的、未取消、未支付」的订单；支付后状态仍是待接单，等员工接单。
     */
    @Override
    public OrderVO payOrder(Long orderId, PayOrderDTO dto) {
        Order order = requireOwnOrder(orderId, dto.getCustomerId());

        if (order.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new BusinessException("订单已取消，无法支付");
        }
        if (order.getPayMethod() != null) {
            throw new BusinessException("订单已支付，请勿重复支付");
        }

        PayMethodEnum method = PayMethodEnum.of(dto.getPayMethod());
        order.setPayMethod(method.getCode());
        order.setPaidTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return buildVO(order);
    }

    /**
     * 取消订单（8 号接口）。
     * <p>
     * 只有「待接单」的订单可以取消（出餐后就不该再取消），取消时把每项数量回补到库存。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO cancelOrder(Long orderId, CancelOrderDTO dto) {
        Order order = requireOwnOrder(orderId, dto.getCustomerId());

        if (order.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new BusinessException("订单已取消，请勿重复操作");
        }
        if (order.getStatus() != OrderStatusEnum.WAIT_FOR_COMMIT) {
            throw new BusinessException("只有「待接单」的订单可以取消，当前状态："
                    + order.getStatus().getDescription());
        }

        // 回补库存
        for (OrderItem item : listItems(orderId)) {
            dishMapper.update(null, new LambdaUpdateWrapper<Dish>()
                    .setSql("stock = stock + " + item.getQuantity())
                    .eq(Dish::getDishId, item.getDishId()));
        }

        order.setStatus(OrderStatusEnum.CANCELLED);
        orderMapper.updateById(order);
        return buildVO(order);
    }

    // ==================== 私有工具方法 ====================

    /** 查出订单并校验存在性与归属 */
    private Order requireOwnOrder(Long orderId, Long customerId) {
        if (orderId == null) {
            throw new BusinessException("订单编号不能为空");
        }
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在：" + orderId);
        }
        if (!Objects.equals(order.getCustomerId(), customerId)) {
            throw new BusinessException("只能操作自己的订单");
        }
        return order;
    }

    /**
     * 解析要用的折扣规则。
     * <p>
     * 传了 ruleId 就校验这一条是否可用；没传则自动挑选「满足门槛、在有效期内、折扣力度最大」的一条。
     */
    private DiscountRule resolveDiscountRule(Long ruleId, BigDecimal total) {
        LocalDateTime now = LocalDateTime.now();

        // Swagger 生成的示例里 Long 字段会默认填 0，这里把 0 视为「不指定规则」，走自动匹配
        if (ruleId != null && ruleId <= 0) {
            ruleId = null;
        }

        if (ruleId != null) {
            DiscountRule rule = discountRuleMapper.selectById(ruleId);
            if (rule == null) {
                throw new BusinessException("折扣规则不存在：" + ruleId);
            }
            if (rule.getStatus() != DiscountStatusEnum.ENABLED) {
                throw new BusinessException("折扣规则已停用：" + rule.getRuleName());
            }
            if (rule.getThreshold() != null && total.compareTo(rule.getThreshold()) < 0) {
                throw new BusinessException("未达到折扣门槛（满 " + rule.getThreshold() + " 元）：" + rule.getRuleName());
            }
            if (rule.getStartTime() != null && now.isBefore(rule.getStartTime())) {
                throw new BusinessException("折扣规则尚未生效：" + rule.getRuleName());
            }
            if (rule.getEndTime() != null && now.isAfter(rule.getEndTime())) {
                throw new BusinessException("折扣规则已过期：" + rule.getRuleName());
            }
            return rule;
        }

        // 自动匹配：门槛已满、状态启用、在有效期内，取折扣率（实付比例）最低的一条
        return discountRuleMapper.selectOne(new LambdaQueryWrapper<DiscountRule>()
                .eq(DiscountRule::getStatus, DiscountStatusEnum.ENABLED)
                .le(DiscountRule::getThreshold, total)
                .and(w -> w.isNull(DiscountRule::getStartTime).or().le(DiscountRule::getStartTime, now))
                .and(w -> w.isNull(DiscountRule::getEndTime).or().ge(DiscountRule::getEndTime, now))
                .orderByAsc(DiscountRule::getDiscountRate)
                .last("LIMIT 1"));
    }

    /** 查询订单明细 */
    private List<OrderItem> listItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    /** 组装单品返回结构 */
    private OrderItemVO toItemVO(Dish dish, Integer quantity, BigDecimal unitPrice) {
        OrderItemVO vo = new OrderItemVO();
        vo.setDishId(dish.getDishId());
        vo.setDishName(dish.getName());
        vo.setQuantity(quantity);
        vo.setUnitPrice(unitPrice);
        vo.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
        return vo;
    }

    /** 用订单 + 明细组装返回值 */
    private OrderVO buildVO(Order order) {
        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : listItems(order.getOrderId())) {
            Dish dish = dishMapper.selectById(item.getDishId());
            OrderItemVO vo = new OrderItemVO();
            vo.setDishId(item.getDishId());
            vo.setDishName(dish == null ? null : dish.getName());
            vo.setQuantity(item.getQuantity());
            vo.setUnitPrice(item.getPriceSnapshot());
            vo.setSubtotal(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            itemVOs.add(vo);
        }
        DiscountRule rule = order.getDiscountRuleId() == null
                ? null : discountRuleMapper.selectById(order.getDiscountRuleId());
        return toVO(order, rule, itemVOs);
    }

    /** 最终组装 */
    private OrderVO toVO(Order order, DiscountRule rule, List<OrderItemVO> items) {
        OrderVO vo = new OrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setCustomerId(order.getCustomerId());
        vo.setWindowId(order.getWindowId());
        vo.setTableNo(order.getTableNo());
        vo.setStatus(order.getStatus() == null ? null : order.getStatus().getCode());
        vo.setStatusDesc(order.getStatus() == null ? null : order.getStatus().getDescription());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayableAmount(order.getPayableAmount());
        vo.setDiscountRuleId(order.getDiscountRuleId());
        vo.setDiscountRuleName(rule == null ? null : rule.getRuleName());
        vo.setPayMethod(order.getPayMethod());
        vo.setPayMethodDesc(order.getPayMethod() == null ? null : PayMethodEnum.of(order.getPayMethod()).getDescription());
        vo.setPaidTime(order.getPaidTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items);
        return vo;
    }
}
