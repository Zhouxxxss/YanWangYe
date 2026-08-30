package com.yanyan.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应体。一期所有接口返回 R，前端根据 code 判断。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

    public static final int SUCCESS = 0;
    public static final int ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;

    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok() {
        return new R<>(SUCCESS, "success", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, "success", data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(SUCCESS, message, data);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ERROR, message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> unauthorized(String message) {
        return new R<>(UNAUTHORIZED, message, null);
    }

    public static <T> R<T> forbidden(String message) {
        return new R<>(FORBIDDEN, message, null);
    }

    /**
     * 分页结果封装（data 内嵌 list + total，前端 tanstack-query 直接消费）
     */
    public static <T> R<PageView<T>> page(Page<T> page) {
        return ok(new PageView<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageView<T> implements Serializable {
        private java.util.List<T> list;
        private long total;
        private long current;
        private long size;
    }
}