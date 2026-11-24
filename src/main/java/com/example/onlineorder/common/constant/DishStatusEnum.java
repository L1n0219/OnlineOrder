package com.example.onlineorder.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.onlineorder.common.exception.BusinessException;

/**
 * 菜品状态枚举。
 * <p>
 * 对应 dish 表的 status 字段（TINYINT）：0 下架，1 上架。
 * 用 @EnumValue 标记 code，MyBatis-Plus 会以 code 值存库/查库。
 */
public enum DishStatusEnum {
    OFF_SALE(0, "下架"),
    ON_SALE(1, "上架");

    /** 存到数据库的值（对应 TINYINT） */
    @EnumValue
    private final int code;

    /** 状态描述 */
    private final String description;

    DishStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /** 根据 code 反查枚举，找不到抛出 BusinessException */
    public static DishStatusEnum of(int code) {
        for (DishStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new BusinessException("未知的菜品状态：" + code);
    }
}
