package com.example.onlineorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineorder.pojo.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface DishMapper extends BaseMapper<Dish> {
    // 扣库存：只有 stock >= num 才执行，返回受影响行数（0 = 库存不足）
    @Update("UPDATE dish SET stock = stock - #{num} " +
            "WHERE dishId = #{dishId} AND stock >= #{num}")
    int deductStock(@Param("dishId") Long dishId,@Param("num") Integer num);

    // 回补库存：取消订单时加回
    @Update("UPDATE dish SET stock = stock + #{num} " +
            "WHERE dish_id = #{dishId}")
    int addStock(@Param("dishId") Long dishId, @Param("num") Integer num);
}
