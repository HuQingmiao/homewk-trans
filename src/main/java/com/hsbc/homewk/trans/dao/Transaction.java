package com.hsbc.homewk.trans.dao;

import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.service.IdGenerator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
public class Transaction {

     private String id;

    private String accountNo;

    private BigDecimal amount;

    private String currency;

    private String transType;

    private String transChannel;

    private LocalDateTime transTime;

    private BigDecimal balanceAfter; // 交易后余额


    public Transaction(TransactionNewCmd transactionNewCmd) {
        this.id = IdGenerator.nextId();
        this.accountNo = transactionNewCmd.accountNo();
        this.amount = transactionNewCmd.amount();
        this.currency = transactionNewCmd.currency();
        this.transType = transactionNewCmd.transType();
        this.transChannel = transactionNewCmd.transChannel();
        this.transTime = transactionNewCmd.transTime();
        this.balanceAfter = transactionNewCmd.balanceAfter();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransChannel() {
        return transChannel;
    }

    public void setTransChannel(String transChannel) {
        this.transChannel = transChannel;
    }

    public LocalDateTime getTransTime() {
        return transTime;
    }

    public void setTransTime(LocalDateTime transTime) {
        this.transTime = transTime;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
