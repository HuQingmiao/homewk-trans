package com.hsbc.homewk.trans.api.cqe;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Command Object for 新增交易记录.  这里不会有 id 字段。
 *
 * @param accountNo
 * @param amount
 * @param currency
 * @param transType
 * @param transTime
 * @param transChannel
 * @param balanceAfter
 */
public record TransactionNewCmd(

        @Size(min = 10, max = 20, message = "账号长度需在10到20位之间")
        String accountNo,

        @Positive(message = "金额必须为正数")
        BigDecimal amount,

        @Pattern(regexp = "^[A-Z]{3}$", message = "货币代码必须是3位大写字母")
        String currency,

        @NotNull(message = "交易类型不能为空")
        String transType,

        @NotNull(message = "交易渠道不能为空")
        String transChannel,

        @PastOrPresent(message = "交易时间不能是未来时间")
        LocalDateTime transTime,

        @DecimalMin(value = "0", message = "余额必须为正数")
        BigDecimal balanceAfter // 交易后余额

) {

    public String getAccountNo() {
        return accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;

    }

    public String getTransType() {
        return transType;
    }

    public String getTransChannel() {
        return transChannel;
    }

    public LocalDateTime getTransTime() {
        return transTime;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
}