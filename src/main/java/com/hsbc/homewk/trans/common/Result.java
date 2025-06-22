package com.hsbc.homewk.trans.common;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import static com.hsbc.homewk.trans.common.ErrorCode.DEFAULT;
import static com.hsbc.homewk.trans.common.ErrorCode.ERR_UNKNOWN;

public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7164110058968651923L;

    private T data;

    private String errorCode;

    private String errorMsg;

    public Result() {
    }

    private Result(T data) {
        this.data = data;
        this.errorCode = DEFAULT.getCode();
        this.errorMsg = "";
    }

    private Result(String errorCode, String errorMsg) {
        this.data = null;
        this.errorCode = StringUtils.isBlank(errorCode) ? ERR_UNKNOWN.getCode() : errorCode;
        this.errorMsg = errorMsg == null ? "" : errorMsg;
    }

    private Result(Exception e) {
        this.data = null;
        this.errorCode = ErrorCode.ERR_UNKNOWN.getCode();
        this.errorMsg = e.getMessage();
    }

    public static <T> Result<T> succ(T data) {
        return new Result<T>(data);
    }

    public static <T> Result<T> fail(Exception e) {
        return new Result<T>(e);
    }

    public static <T> Result<T> fail(String errorCode, String errorMsg) {
        return new Result<T>(errorCode, errorMsg);
    }

    public boolean isSuccess() {
        return DEFAULT.getCode().equals(this.errorCode);
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}