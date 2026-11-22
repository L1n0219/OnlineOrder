package com.example.onlineorder.common.exception;

import com.example.onlineorder.common.Result;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常。
 * <p>
 * 业务逻辑中可以抛出的异常（例如"用户不存在""余额不足"），
 * 它不会被当成系统故障记录错误堆栈，而是直接把 message 返回给前端。
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务异常默认使用 400 表示"是你能看懂的错" */
    private final int code;

    public BusinessException(String message) {
        this(Result.CODE_BAD_REQUEST, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

}
