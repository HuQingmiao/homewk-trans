package com.hsbc.homewk.trans.dao;

import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.api.cqe.TransactionUpdCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 提供交易记录的存储及索引结构, 以及相关的数据操作接口
 *
 * @author HuQingmiao
 */
@Component
public class TransactionRepository {
    private final static Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    // 用于存储交易记录
    private final static Map<String, Transaction> store = new HashMap<>();

    // 用于索引交易记录，索引字段为accuntNo
    private final static Map<String, List<Transaction>> accountNoIdxMap = new HashMap<>();

    // 用于索引交易记录，索引字段为transType
    private final static Map<String, List<Transaction>> transTypeIdxMap = new HashMap<>();

    // 用于索引交易记录，索引字段为transChannel
    private final static Map<String, List<Transaction>> transChannelIdxMap = new HashMap<>();


    public Transaction save(TransactionNewCmd transactionNewCmd) {
        log.debug(">> save to store: {}", transactionNewCmd.toString());
        Transaction t = new Transaction(transactionNewCmd);
        store.put(t.getId(), t);

        accountNoIdxMap.computeIfAbsent(t.getAccountNo(), k -> new ArrayList<>());
        accountNoIdxMap.get(t.getAccountNo()).add(t);

        transTypeIdxMap.computeIfAbsent(t.getTransType(), k -> new ArrayList<>());
        transTypeIdxMap.get(t.getTransType()).add(t);

        transChannelIdxMap.computeIfAbsent(t.getTransChannel(), k -> new ArrayList<>());
        transChannelIdxMap.get(t.getTransChannel()).add(t);

        log.info(" the size of store: {}", store.size());
        return t;
    }


    public boolean delete(String id) {
        log.debug(">> delete from store, id= {}", id);
        Transaction t = store.get(id);
        if (t == null) {
            return false;
        }

        // 从存储中删除
        store.remove(id);
        log.info(" the size of store: {}", store.size());

        // 从索引中删除
        List<Transaction> tList = accountNoIdxMap.get(t.getAccountNo());
        if (tList != null) {
            tList.remove(t);
        }
        tList = transTypeIdxMap.get(t.getTransType());
        if (tList != null) {
            tList.remove(t);
        }
        tList = transChannelIdxMap.get(t.getTransChannel());
        if (tList != null) {
            tList.remove(t);
        }
        return true;
    }

    public Transaction findById(String id) {
        return store.get(id);
    }

    /**
     * 更新交易记录。 (根据通常的业务规则，本方法不会交易时间进行更改)
     *
     * @param transactionUpdCmd
     * @return
     */
    public Transaction update(TransactionUpdCmd transactionUpdCmd) {
        log.debug(">> update on store: {}", transactionUpdCmd.toString());
        Transaction t = store.get(transactionUpdCmd.id());
        if (t == null) {
            throw new IllegalArgumentException("交易记录不存在");
        }

        // 从索引中删除旧的索引
        List<Transaction> tList = accountNoIdxMap.get(t.getAccountNo());
        if (tList != null) {
            tList.remove(t);
        }
        tList = transTypeIdxMap.get(t.getTransType());
        if (tList != null) {
            tList.remove(t);
        }
        tList = transChannelIdxMap.get(t.getTransChannel());
        if (tList != null) {
            tList.remove(t);
        }

        // 为修改后的交易设置索引
        accountNoIdxMap.computeIfAbsent(transactionUpdCmd.getAccountNo(), k -> new ArrayList<>());
        accountNoIdxMap.get(transactionUpdCmd.getAccountNo()).add(t);

        transTypeIdxMap.computeIfAbsent(transactionUpdCmd.getTransType(), k -> new ArrayList<>());
        transTypeIdxMap.get(transactionUpdCmd.getTransType()).add(t);

        transChannelIdxMap.computeIfAbsent(transactionUpdCmd.getTransChannel(), k -> new ArrayList<>());
        transChannelIdxMap.get(transactionUpdCmd.getTransChannel()).add(t);

        // 更新交易记录
        t.setAccountNo(transactionUpdCmd.accountNo());
        t.setAmount(transactionUpdCmd.amount());
        t.setCurrency(transactionUpdCmd.currency());
        t.setTransType(transactionUpdCmd.transType());
        t.setTransChannel(transactionUpdCmd.transChannel());
        t.setTransTime(transactionUpdCmd.transTime());
        t.setBalanceAfter(transactionUpdCmd.balanceAfter());

        return t;
    }


    public ArrayList<Transaction> findByAccountNo(String accountNo, LocalDateTime transBeginTime, LocalDateTime transEndTime) {
        log.info(">> query on store, accountNo= {}", accountNo);
        // 先从索引中查找, 即根据字段值找到对应的交易记录
        List<Transaction> tList = null;
        if("".equals(accountNo.trim())){
            tList = accountNoIdxMap.values().stream().flatMap(List::stream).toList();
        }else{
            tList = accountNoIdxMap.get(accountNo);
        }
        if (tList == null) {
            return new ArrayList<>();
        }
        log.info("<< size: {}", tList.size());

        // 再根据时间范围过滤，并按时间排序
        return this.filterByTransTime(tList, transBeginTime, transEndTime);
    }

    public ArrayList<Transaction> findByTransType(String transType, LocalDateTime transBeginTime, LocalDateTime transEndTime) {
        log.info(">> query on store, transType= {}", transType);
        // 从索引中查找, 即根据字段值找到对应的交易记录
        List<Transaction> tList = null;
        if("".equals(transType.trim())){
            tList = transTypeIdxMap.values().stream().flatMap(List::stream).toList();
        }else{
            tList = transTypeIdxMap.get(transType);
        }
        if (tList == null) {
            return new ArrayList<>();
        }
        log.info("<< size: {}", tList.size());

        // 再根据时间范围过滤，并按时间排序
        return this.filterByTransTime(tList, transBeginTime, transEndTime);
    }

    public ArrayList<Transaction> findByTransChannel(String transChannel, LocalDateTime transBeginTime, LocalDateTime transEndTime) {
        log.info(">> query on store, transChannel= {}", transChannel);
        // 从索引中查找, 即根据字段值找到对应的交易记录
        List<Transaction> tList = null;
        if("".equals(transChannel.trim())){
            tList = transChannelIdxMap.values().stream().flatMap(List::stream).toList();
        }else{
            tList = transChannelIdxMap.get(transChannel);
        }
        if (tList == null) {
            return new ArrayList<>();
        }
        log.info("<< size: {}", tList.size());

        // 再根据时间范围过滤，并按时间排序
        return this.filterByTransTime(tList, transBeginTime, transEndTime);
    }


    private ArrayList<Transaction> filterByTransTime(List<Transaction> transList, LocalDateTime transBeginTime, LocalDateTime transEndTime) {
        // 根据交易时间过滤
        ArrayList<Transaction> retList = new ArrayList<>();
        for (Transaction t : transList) {
            if (t.getTransTime().isAfter(transBeginTime) && t.getTransTime().isBefore(transEndTime)) {
                retList.add(t);
            }
        }

        // 按交易时间倒序
        Collections.sort(retList, (t1, t2) -> t2.getTransTime().compareTo(t1.getTransTime()));
        return retList;
    }

    public static void main(String[] args) {
        TransactionNewCmd tnc = new TransactionNewCmd("1234567890", new BigDecimal("1000"), "USD", "deposit", "bank", LocalDateTime.now(), new BigDecimal("1000"));
        //System.out.println(tnc);

        TransactionRepository service = new TransactionRepository();
        Transaction t = service.save(tnc);
        System.out.println(store.get(t.getId()));

        List<Transaction> transList = accountNoIdxMap.get(tnc.accountNo());
        for (Transaction t1 : transList) {
            System.out.println(">>1 " + t1);
        }

        TransactionUpdCmd tuc = new TransactionUpdCmd(t.getId(), "1234567890", new BigDecimal("1053400"), "USD", "deposit", "bank", LocalDateTime.now(), new BigDecimal("1000"));

        t = service.update(tuc);
        //System.out.println(store.get(t.id()));

        transList = accountNoIdxMap.get(tuc.accountNo());
        for (Transaction t1 : transList) {
            System.out.println(">>2 " + t1);
        }

        tnc = new TransactionNewCmd("1234567890", new BigDecimal("10033"), "USD", "deposit", "bank", LocalDateTime.now(), new BigDecimal("1000"));
        t = service.save(tnc);

        tnc = new TransactionNewCmd("1234567890", new BigDecimal("10344"), "USD", "deposit", "bank", LocalDateTime.now().plusDays(1), new BigDecimal("1000"));
        t = service.save(tnc);

        tnc = new TransactionNewCmd("1234567890", new BigDecimal("1066"), "USD", "deposit", "bank", LocalDateTime.now().plusMinutes(20), new BigDecimal("1000"));
        t = service.save(tnc);


        tnc = new TransactionNewCmd("1234567890", new BigDecimal("100667"), "USD", "deposit", "bank", LocalDateTime.now(), new BigDecimal("1000"));
        t = service.save(tnc);

        tnc = new TransactionNewCmd("1234567890", new BigDecimal("777"), "USD", "deposit", "bank", LocalDateTime.now(), new BigDecimal("1000"));
        t = service.save(tnc);


        transList = accountNoIdxMap.get(t.getAccountNo());
        for (Transaction t1 : transList) {
            System.out.println(">>3 " + t1);
        }

        transList = service.findByAccountNo(t.getAccountNo(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(1));
        for (Transaction t1 : transList) {
            System.out.println(">>4 " + t1);
        }

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

        LocalDateTime dateTime2 = LocalDateTime.parse("2025-06-22T08:24:23.913923700", customFormatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse("2025-06-21T09:24:23.915921600", customFormatter);
        if (dateTime2.isBefore(dateTimeEnd)) {
            System.out.println("true");
        }
        System.out.println("false");
    }
}


