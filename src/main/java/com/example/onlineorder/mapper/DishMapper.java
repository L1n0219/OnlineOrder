package com.example.onlineorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineorder.pojo.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 菜品表（dish）的 Mapper 接口。
 * <p>
 * 继承 {@link BaseMapper}<{@link Dish}> 获得通用增删改查；
 * 另定义扣减/回补库存的自定义 SQL。
 */
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 扣减库存：仅当 stock >= num 时才执行，返回受影响行数（0 表示库存不足）。
     */
    @Update("UPDATE dish SET stock = stock - #{num} " +
            "WHERE dish_id = #{dishId} AND stock >= #{num}")
    int deductStock(@Param("dishId") Long dishId, @Param("num") Integer num);

    /**
     * 回补库存：取消订单时加回。
     */
    @Update("UPDATE dish SET stock = stock + #{num} " +
            "WHERE dish_id = #{dishId}")
    int addStock(@Param("dishId") Long dishId, @Param("num") Integer num);
}
