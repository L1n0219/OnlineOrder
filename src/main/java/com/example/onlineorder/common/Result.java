package com.example.onlineorder.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装类。
 * <p>
 * 有了它，所有接口都返回同一种 JSON 结构，前端只需判断 code 就能区分成功/失败：
 * <pre>
 * { "code": 200, "msg": "操作成功", "data": { ... } }
 * </pre>
 *
 * @param <T> 业务数据的类型
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int CODE_SUCCESS = 200;

    /** 参数错误 / 业务规则不满足（用户可以自己改的那种错） */
    public static final int CODE_BAD_REQUEST = 400;

    /** 服务器内部错误（用户改不了，需要看日志的那种错） */
    public static final int CODE_ERROR = 500;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 业务数据，失败时为 null */
    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ==================== 成功 ====================

    /** 成功，无数据 */
    public static <T> Result<T> ok() {
        return new Result<>(CODE_SUCCESS, "操作成功", null);
    }

    /** 成功，带数据 */
    public static <T> Result<T> ok(T data) {
        return new Result<>(CODE_SUCCESS, "操作成功", data);
    }

    /** 成功，自定义提示 + 数据 */
    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(CODE_SUCCESS, msg, data);
    }

    // ==================== 失败 ====================

    /** 失败，默认 500 与通用提示 */
    public static <T> Result<T> fail() {
        return new Result<>(CODE_ERROR, "操作失败", null);
    }

    /** 失败，只给提示（业务失败，默认 500） */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(CODE_ERROR, msg, null);
    }

    /** 失败，自定义状态码 + 提示 */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 是否成功，方便在 Service / 测试里判断。
     * <p>
     * 加 @JsonIgnore 是因为 Jackson 会按 JavaBean 规范把 isSuccess() 当成属性，
     * 序列化出多余的 "success" 字段；返回给前端的结构只保留 code / msg / data。
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.code == CODE_SUCCESS;
    }
}
