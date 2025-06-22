package com.hsbc.homewk.trans.api;


import com.alibaba.fastjson.JSONObject;
import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.api.cqe.TransactionUpdCmd;
import com.hsbc.homewk.trans.api.cqe.TranscationQuery;
import com.hsbc.homewk.trans.common.Result;
import com.hsbc.homewk.trans.common.enums.TransChannel;
import com.hsbc.homewk.trans.common.enums.TransType;
import com.hsbc.homewk.trans.common.util.HttpHelper;
import com.hsbc.homewk.trans.common.util.WebPageList;
import com.hsbc.homewk.trans.dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TransactionAPITest {
    private final static Logger log = LoggerFactory.getLogger(TransactionAPITest.class);

    private static final String API_URL = "http://localhost:9090";

   // @BeforeEach
   @Test
    public void setUp() throws Exception {
        log.info(">>> setUp ");
        String apiUrl = API_URL + "/api/transaction";
        LocalDateTime transTime = LocalDateTime.now();
        for (int i = 0; i < 29; i++) {
            TransactionNewCmd cmd = new TransactionNewCmd("1234567890", new BigDecimal("1000"), "USD",
                    TransType.DEPOSIT.name(), TransChannel.ATM.name(), transTime.plusHours(i), new BigDecimal("10000"));

            String retStr = HttpHelper.post(apiUrl, null, null, cmd);
            log.info("return: " + retStr);
        }
    }

    @Test
    public void findTransactions() throws Exception {
        log.info(">>> findTransactions");
        String apiUrl = API_URL + "/api/transaction";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime txBeginTime = LocalDateTime.parse("2025-06-11 10:17:10", formatter);
        LocalDateTime txEndTime = LocalDateTime.parse("2025-06-25 19:17:10", formatter);

        Map<String, Object> paramerMap = new HashMap<>();
        paramerMap.put("qryField", TranscationQuery.QRY_FIELD_TRANS_TYPE);
        paramerMap.put("qryText", "");
        paramerMap.put("transBeginTime", txBeginTime);
        paramerMap.put("transEndTime", txEndTime);
        paramerMap.put("pageNo", 1);
        paramerMap.put("pageSize", 10);

        String retStr = HttpHelper.get(apiUrl, null, paramerMap);
        log.info("return: " + retStr);

//        paramerMap.put("pageNo", 2);
//        retStr = HttpHelper.get(apiUrl, null, paramerMap);
//        log.info("return: " + retStr);
    }

    public void saveTransaction() throws Exception {
        log.info(">>> saveTransaction ");
        String apiUrl = API_URL + "/api/transaction";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime txTime = LocalDateTime.parse("2025-06-22 10:17:10", formatter);

        TransactionNewCmd cmd = new TransactionNewCmd("1234567890", new BigDecimal("1000"), "USD",
                TransType.DEPOSIT.name(), TransChannel.ATM.name(), txTime, new BigDecimal("10000"));

        String retStr = HttpHelper.post(apiUrl, null, null, cmd);
        log.info("return: " + retStr);
    }

    @Test
    public void updateTransaction() throws Exception {
        log.info(">>> updateTransaction");
        String id = "2025062206425900000";
        String apiUrl = API_URL + "/api/transaction" + "/" + id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime txTime = LocalDateTime.parse("2025-06-21 10:17:10", formatter);

        TransactionUpdCmd cmd = new TransactionUpdCmd("2025062206425900000", "1234567890", new BigDecimal("1000"), "USD",
                TransType.WITHDRAWAL.name(), TransChannel.OTHER.name(), txTime, new BigDecimal("333333"));

        String retStr = HttpHelper.put(apiUrl, null, null, cmd);
        log.info("return: " + retStr);
    }

    @Test
    public void deleteTransactions() throws Exception {
        log.info(">>> deleteTransactions");
        String id = "2025062206445800002";
        String apiUrl = API_URL + "/api/transaction" + "/" + id;

        Map<String, Object> paramerMap = new HashMap<>();
        String retStr = HttpHelper.delete(apiUrl, null, paramerMap);
        log.info("return: " + retStr);
    }
}
