package com.example.onlineorder.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.onlineorder.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 支付方式枚举，对应 orders 表的 pay_method 字段（TINYINT）。
 * <p>
 * NULL 表示未支付，所以这里只列已支付时的取值。
 */
public enum PayMethodEnum {

    WECHAT(1, "微信"),
    ALIPAY(2, "支付宝"),
    OTHER(3, "其他");

    @EnumValue
    @JsonValue
    private final int code;

    private final String description;

    PayMethodEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /** 根据 code 反查枚举，找不到抛出 BusinessException */
    public static PayMethodEnum of(int code) {
        for (PayMethodEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        throw new BusinessException("不支持的支付方式：" + code + "（1 微信 / 2 支付宝 / 3 其他）");
    }
}
