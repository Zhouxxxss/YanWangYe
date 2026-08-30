package com.yanyan.common.exception;

/**
 * 业务异常。抛出后由全局异常处理器统一转换为 R。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = com.yanyan.common.result.R.ERROR;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}