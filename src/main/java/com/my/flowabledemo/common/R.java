package com.my.flowabledemo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回体
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    /** 时间戳 */
    private long timestamp = System.currentTimeMillis();

    private R() {}

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回
     */
    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回
     */
    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMessage(), null);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.FAILURE.getCode(), message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 自定义状态码返回
     */
    public static <T> R<T> of(int code, String message, T data) {
        return new R<>(code, message, data);
    }
}