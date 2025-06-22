package com.hsbc.homewk.trans.service;

import com.hsbc.homewk.trans.TransApplication;
import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.api.cqe.TranscationQuery;
import com.hsbc.homewk.trans.common.enums.TransChannel;
import com.hsbc.homewk.trans.common.enums.TransType;
import com.hsbc.homewk.trans.common.util.PageList;
import com.hsbc.homewk.trans.dao.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest(classes = TransApplication.class)
public class TransactionServiceTest {
    private final static Logger log = LoggerFactory.getLogger(TransactionServiceTest.class);

    @Autowired
    private TransactionService service;

    @BeforeEach
    public void setUp() {
        LocalDateTime transTime = LocalDateTime.now();

        for (int i = 0; i < 10; i++) {
            TransactionNewCmd cmd1 = new TransactionNewCmd("1234567890", new BigDecimal("1000"), "USD",
                    TransType.DEPOSIT.name(), TransChannel.ATM.name(), transTime.plusDays((long) (Math.random() * 10)), new BigDecimal("10000"));
            service.save("aa1222", cmd1);

            TransactionNewCmd cmd2 = new TransactionNewCmd("1234567890", new BigDecimal("1000"), "USD",
                    TransType.WITHDRAWAL.name(), TransChannel.MOBILE_APP.name(), transTime.plusDays((long) (Math.random() * 10)), new BigDecimal("10000"));
            service.save("aa1222", cmd2);

            TransactionNewCmd cmd3 = new TransactionNewCmd("1234567892", new BigDecimal("1000"), "USD",
                    TransType.DEPOSIT.name(), TransChannel.ATM.name(), transTime.plusDays((long) (Math.random() * 10)), new BigDecimal("10000"));
            service.save("bbccc", cmd3);

            TransactionNewCmd cmd4 = new TransactionNewCmd("1234567892", new BigDecimal("1000"), "USD",
                    TransType.WITHDRAWAL.name(), TransChannel.MOBILE_APP.name(), transTime.plusDays((long) (Math.random() * 10)), new BigDecimal("10000"));
            service.save("bbccc", cmd4);
        }
    }


    @Test
    public void testCache() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse("2025-06-11 10:17:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2025-06-22 23:17:10", formatter);

        TranscationQuery query = new TranscationQuery(TranscationQuery.QRY_FIELD_TRANS_TYPE,  TransType.WITHDRAWAL.name(), beginTime, endTime);
        PageList<Transaction> ts = service.queryForPager("wwww", query, 1, 10);
        String id= null;

        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
            if(t1.getAccountNo().equals("1234567892")) {
                id = t1.getId();
            }
        }

        TranscationQuery query2 = new TranscationQuery(TranscationQuery.QRY_FIELD_ACCOUNT_NO, "1234567890", beginTime, endTime);
        PageList<Transaction> ts2 = service.queryForPager("wwww", query2, 1, 10);

        for (Transaction t1 : ts2.getDataList()) {
            System.out.println(">> " + t1);
        }
        LocalDateTime transTime = LocalDateTime.now();
        TransactionNewCmd cmd4 = new TransactionNewCmd("1234567892", new BigDecimal("1000"), "USD",
                TransType.WITHDRAWAL.name(), TransChannel.MOBILE_APP.name(), transTime.plusDays((long) (Math.random() * 10)), new BigDecimal("10000"));

     //   service.save("wwww", cmd4);

        TranscationQuery query3 = new TranscationQuery(TranscationQuery.QRY_FIELD_ACCOUNT_NO, "1234567890", beginTime, endTime);
        ts = service.queryForPager("wwww", query3, 1, 10);
        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
        }
    }


     @Test
    public void testQueryByAccountNo() {
        log.info(">>> testQueryByAccountNo");
        TranscationQuery query = new TranscationQuery(TranscationQuery.QRY_FIELD_ACCOUNT_NO, "1234567890", LocalDateTime.now().minusDays(10), LocalDateTime.now());
        PageList<Transaction> ts = service.queryForPager("1234567890", query, 1, 10);
        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
        }

        ts = service.queryForPager("aaa", query, 1, 2);
        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
        }

         ts = service.queryForPager("bbb", query, 2, 2);
         for (Transaction t1 : ts.getDataList()) {
             System.out.println(">> " + t1);
         }
    }

    @Test
    public void testQueryByTransactionType() {
        log.info(">>> testQueryByTransactionType");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse("2025-06-21 10:17:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2025-06-22 19:17:10", formatter);

        TranscationQuery query = new TranscationQuery(TranscationQuery.QRY_FIELD_TRANS_TYPE, TransType.DEPOSIT.name(), LocalDateTime.now().minusDays(10), LocalDateTime.now());
        PageList<Transaction> ts = service.queryForPager("1234567890", query, 1, 10);
        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
        }
    }

    @Test
    public void testQueryByTransactionChannel() {
        log.info(">>> testQueryByTransactionChannel");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse("2025-06-21 10:17:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2025-06-22 19:17:10", formatter);

        TranscationQuery query = new TranscationQuery(TranscationQuery.QRY_FIELD_TRANS_CHANNEL, TransChannel.ATM.name(), LocalDateTime.now().minusDays(10), LocalDateTime.now());
        PageList<Transaction> ts = service.queryForPager("1234567890", query, 1, 10);
        for (Transaction t1 : ts.getDataList()) {
            System.out.println(">> " + t1);
        }
    }
}
