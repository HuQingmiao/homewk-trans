package com.hsbc.homewk.trans.dto;

import com.hsbc.homewk.trans.dao.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction DTO  用于返回给调用方或浏览器的交易信息
 *
 * @param accountNo
 * @param amount
 * @param currency
 * @param transType
 * @param transChannel
 * @param transTime
 * @param balanceAfter
 */
public record TransactionDto(

        String id,

        String accountNo,

        BigDecimal amount,

        String currency,

        String transType,

        String transChannel,

        LocalDateTime transTime,

        BigDecimal balanceAfter // 交易后余额
) {
    // 静态工厂方法
    public static TransactionDto buildFrom(Transaction transaction) {
        return new TransactionDto(transaction.getId(), transaction.getAccountNo(), transaction.getAmount(), transaction.getCurrency(), transaction.getTransType(), transaction.getTransChannel(), transaction.getTransTime(), transaction.getBalanceAfter());
    }

    public String getId() {
        return id;
    }

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