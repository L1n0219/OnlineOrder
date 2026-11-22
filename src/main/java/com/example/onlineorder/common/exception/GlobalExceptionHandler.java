package com.example.onlineorder.common.exception;

import com.example.onlineorder.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author LIN and DeepSeek v4.1 pro
 * 全局异常处理器。
 * <p>
 * 加上 @RestControllerAdvice 后，Controller 里抛出的异常不用再逐个 try-catch，
 * 都会被这里按类型"接住"，统一转成 {@link Result} 返回给前端。
 * <p>
 * 注意：这里只是把错误信息封装进 Result，HTTP 状态码仍然是 200。
 * 如果希望前端/网关按真实状态码处理，可以改用 ResponseEntity 返回，
 * 或在处理异常的方法上加 @ResponseStatus。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：我们自己主动抛的，属于"预期内的错误"
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 唯一键 / 主键冲突，例如重复注册手机号、重复的微信 OpenID、重复用户名。
     * 属于"数据已存在"的业务性错误，不应该被下面的兜底逻辑当成 500 系统故障。
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("数据重复：{}", e.getMessage());
        return Result.fail(Result.CODE_BAD_REQUEST, "数据已存在，请勿重复提交");
    }

    /**
     * 参数校验失败（实体类上标注 @Valid + @NotBlank 之类时触发）
     * 需要先引入 spring-boot-starter-validation 依赖
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        // 只取第一条校验失败的信息返回，避免一次性抛给前端一大串
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        log.warn("参数校验失败：{}", msg);
        return Result.fail(Result.CODE_BAD_REQUEST, msg);
    }

    /**
     * 缺少必填的请求参数，例如 @RequestParam String name 但没传 name
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "缺少必要参数：" + e.getParameterName();
        log.warn(msg);
        return Result.fail(Result.CODE_BAD_REQUEST, msg);
    }

    /**
     * 参数类型转换失败，例如接口参数是 Long 但前端传了 "abc"、日期格式对不上等。
     * 属于参数错误，应返回 400 而不是被兜底逻辑吞成 500。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = "参数类型错误：" + e.getName();
        log.warn(msg);
        return Result.fail(Result.CODE_BAD_REQUEST, msg);
    }

    /**
     * 请求体格式不对，例如 JSON 语法错误、类型对不上
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败：{}", e.getMessage());
        return Result.fail(Result.CODE_BAD_REQUEST, "请求体格式不正确");
    }

    /**
     * 请求方法不支持，例如接口只允许 GET，却用 POST 调
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持：{}", e.getMessage());
        return Result.fail(405, "不支持的请求方法：" + e.getMethod());
    }

    /**
     * 访问了不存在的接口，返回 404 而不是被下面的兜底逻辑吞成 500
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException e) {
        log.warn("接口不存在：{}", e.getResourcePath());
        return Result.fail(404, "接口不存在");
    }

    /**
     * 兜底：所有没被上面匹配到的异常，都属于"预期外的系统错误"
     * 这里不要把 e.getMessage() 直接给前端，只记日志，避免泄露内部实现
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常，请求地址：{}", request.getRequestURI(), e);
        return Result.fail(Result.CODE_ERROR, "系统繁忙，请稍后重试");
    }
}
