package com.hsbc.homewk.trans.api;


import com.hsbc.homewk.trans.common.ResultHandler;
import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.api.cqe.TransactionUpdCmd;
import com.hsbc.homewk.trans.api.cqe.TranscationQuery;
import com.hsbc.homewk.trans.dto.TransactionDto;
import com.hsbc.homewk.trans.common.Result;
import com.hsbc.homewk.trans.dao.Transaction;
import com.hsbc.homewk.trans.service.TransactionService;
import com.hsbc.homewk.trans.common.util.PageList;
import com.hsbc.homewk.trans.common.util.WebPageList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "交易记录管理")
@RestController
@RequestMapping("/api/transaction")
public class TransactionAPI {
    private final static Logger log = LoggerFactory.getLogger(TransactionAPI.class);

    @Autowired
    private TransactionService transService;


    @Operation(summary = "分页查询交易")
    @GetMapping
    @ResultHandler
    public Result<WebPageList<TransactionDto>> findTransactions(@RequestParam String qryField,
                                                                @RequestParam String qryText,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime transBeginTime,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime transEndTime,
                                                                @RequestParam(defaultValue = "1") int pageNo,
                                                                @RequestParam(defaultValue = "10") int pageSize,
                                                                HttpServletRequest httpRequest) {
        log.info(">> find transactions: qryField={}, qryText={}, transBeginTime={}, transEndTime={}, pageNo={}, pageSize={}",
                qryField, qryText, transBeginTime, transEndTime, pageNo, pageSize);

        // 获取用户标识
        String sessionId = this.getOrCreateSessionId(httpRequest);

        // 构造查询条件
        TranscationQuery pageQuery = TranscationQuery.build(qryField, qryText, transBeginTime, transEndTime);

        // 查得当前页的交易记录
        PageList<Transaction> pageList = transService.queryForPager(sessionId, pageQuery, pageNo, pageSize);

        // 将实体类转换为dto对象, 以对外隐藏内部结构
        ArrayList<TransactionDto> dtoList = pageList.getDataList().stream()
                .map(TransactionDto::buildFrom)
                .collect(Collectors.toCollection(ArrayList::new));

        WebPageList<TransactionDto> webPageList = new WebPageList<>(pageList.getTotalCount(), dtoList, pageNo, pageSize);
        return Result.succ(webPageList);
    }

    @Operation(summary = "创建交易记录")
    @PostMapping
    @ResultHandler
    public Result<TransactionDto> addTransaction(@RequestBody TransactionNewCmd transactionNewCmd, HttpServletRequest httpRequest) {
        log.info(">> add transaction, transactionNewCmd = {}", transactionNewCmd);

        String sessionId = this.getOrCreateSessionId(httpRequest);
        Transaction t = transService.save(sessionId, transactionNewCmd);

        // 将实体类转换为dto对象, 以对外隐藏内部结构
        return Result.succ(TransactionDto.buildFrom(t));
    }

    @Operation(summary = "更新交易记录")
    @PutMapping("/{id}")
    @ResultHandler
    public Result<TransactionDto> updateTransaction(@PathVariable String id, @RequestBody TransactionUpdCmd transactionUpdCmd, HttpServletRequest httpRequest) {
        log.info(">> update transaction, transactionUpdCmd = {}", transactionUpdCmd);
        if (id != null && !id.equals(transactionUpdCmd.getId())) {
            log.error("transaction id cannot be the same as the id in the request body");
            return Result.fail("", "交易id与请求体中的交易id不同，本应完全一致。");
        }

        String sessionId = this.getOrCreateSessionId(httpRequest);
        Transaction t = transService.update(sessionId, transactionUpdCmd);

        // 将实体类转换为dto对象, 以对外隐藏内部结构
        return Result.succ(TransactionDto.buildFrom(t));
    }

    @Operation(summary = "删除交易记录")
    @DeleteMapping("/{id}")
    @ResultHandler
    public Result<Void> deleteTransaction(@PathVariable String id, HttpServletRequest httpRequest) {
        log.info(">> delete transaction, id = {}", id);

        String sessionId = this.getOrCreateSessionId(httpRequest);
        transService.delete(sessionId, id);

        return Result.succ(null);
    }

    private String getOrCreateSessionId(HttpServletRequest request) {
        final String sessionKey = "anonymousId";
        HttpSession session = request.getSession(true);

        // 如果已有session且包含ID，直接返回
        if (session.getAttribute(sessionKey) != null) {
            return (String) session.getAttribute(sessionKey);
        }

        // 首次访问，创建新session和ID
        String newId = UUID.randomUUID().toString();
        session.setAttribute(sessionKey, newId);
        session.setMaxInactiveInterval(2 * 60 * 60); // 2小时有效期
        return newId;
    }
}
