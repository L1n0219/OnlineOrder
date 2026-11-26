package com.example.onlineorder.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.onlineorder.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 折扣规则状态枚举。
 * <p>
 * 对应 discount_rule 表的 status 字段（TINYINT）：0 停用，1 启用。
 * 用 @EnumValue 标记 code，MyBatis-Plus 会以 code 值存库/查库。
 */
public enum DiscountStatusEnum {
    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    /** 存到数据库的值（对应 TINYINT） */
    @EnumValue
    @JsonValue
    private final int code;

    /** 状态描述 */
    private final String description;

    DiscountStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /** 根据 code 反查枚举，找不到抛出 BusinessException */
    public static DiscountStatusEnum of(int code) {
        for (DiscountStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new BusinessException("未知的折扣规则状态：" + code);
    }
}
