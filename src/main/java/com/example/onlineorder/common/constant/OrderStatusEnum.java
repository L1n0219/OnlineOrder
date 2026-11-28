package com.example.onlineorder.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.onlineorder.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatusEnum {
    WAIT_FOR_COMMIT(0,"待接单"),
    PREPARING(1,"备餐中"),
    READY(2,"已出餐"),
    COMPLETED(3,"已完成"),
    CANCELLED(4,"已取消");

    /** 存到数据库的值（对应 TINYINT） */
    @EnumValue
    @JsonValue
    private final int code;

    /** 状态描述 */
    private final String description;

    OrderStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /** 根据 code 反查枚举，找不到抛出 BusinessException */
    public static OrderStatusEnum of(int code) {
        for (OrderStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new BusinessException("未知的菜品状态：" + code);
    }
}
