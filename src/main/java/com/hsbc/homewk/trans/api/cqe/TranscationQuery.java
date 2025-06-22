package com.hsbc.homewk.trans.api.cqe;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Query Object for 综合查询
 */
public record TranscationQuery(

        @Size(min = 10, max = 20, message = "必须选择一项主要的查询条件")
        String qryField,

        @NotNull
        String qryText,

        @PastOrPresent(message = "交易的起始日期必须是过去的时间")
        LocalDateTime transBeginTime,

        @PastOrPresent(message = "交易的结束日期必须是以后的时间")
        LocalDateTime transEndTime
) {
    final public static String QRY_FIELD_ACCOUNT_NO = "accountNo";
    final public static String QRY_FIELD_TRANS_TYPE = "transType";
    final public static String QRY_FIELD_TRANS_CHANNEL = "transChannel";

    // 静态工厂方法
    public static TranscationQuery build(String qryField, String qryText, LocalDateTime transBeginTime, LocalDateTime transEndTime) {
        return new TranscationQuery(qryField, qryText, transBeginTime, transEndTime);
    }

}
