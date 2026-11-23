package com.example.onlineorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineorder.pojo.VO.admin.TopDishVO;
import com.example.onlineorder.pojo.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单明细表（order_item）的 Mapper 接口。
 * <p>
 * 继承 {@link BaseMapper}<{@link OrderItem}> 获得订单明细的通用增删改查能力；
 * 另定义热销菜品排行等需要联表查询的方法。
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 热销菜品排行：联查 dish 取菜名，按销量降序取前 N 名。
     */
    @Select("SELECT d.name AS dishName, SUM(oi.quantity) AS totalQuantity " +
            "FROM order_item oi JOIN dish d ON oi.dish_id = d.dish_id " +
            "GROUP BY oi.dish_id, d.name " +
            "ORDER BY totalQuantity DESC LIMIT #{limit}")
    List<TopDishVO> topDishes(@Param("limit") int limit);
}
