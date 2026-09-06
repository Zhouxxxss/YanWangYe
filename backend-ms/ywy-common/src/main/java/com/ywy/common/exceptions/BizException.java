package com.ywy.common.exceptions;

import com.ywy.common.result.R;

/**
 * 业务异常。抛出后由全局异常处理器统一转换为 R。各业务模块复用同一套异常。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = R.ERROR;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}