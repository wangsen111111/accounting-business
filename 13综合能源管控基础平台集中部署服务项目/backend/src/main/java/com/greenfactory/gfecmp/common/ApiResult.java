package com.greenfactory.gfecmp.common;

/**
 * 统一响应结构：{ code, message, data, timestamp }
 * code = 0 表示成功。
 */
public record ApiResult<T>(int code, String message, T data, long timestamp) {

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "ok", data, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null, System.currentTimeMillis());
    }
}
