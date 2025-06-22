package com.hsbc.homewk.trans.common;

/**
 * 本模块的错误码， 仅供Result及自定义的异常类引用。
 *
 * 本Enum只能是包访问权限，不能设为public。
 *
 * @author HuQingmiao
 */
 enum ErrorCode {
    DEFAULT("0000", "调用成功"),

    ERR_UNKNOWN("9999", "调用失败：未知错误");


    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private ErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
