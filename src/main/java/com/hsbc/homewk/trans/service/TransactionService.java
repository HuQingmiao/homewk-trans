package com.hsbc.homewk.trans.service;

import com.hsbc.homewk.trans.api.cqe.TransactionNewCmd;
import com.hsbc.homewk.trans.api.cqe.TransactionUpdCmd;
import com.hsbc.homewk.trans.api.cqe.TranscationQuery;
import com.hsbc.homewk.trans.common.util.PageList;
import com.hsbc.homewk.trans.dao.Transaction;
import com.hsbc.homewk.trans.dao.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 核心业务类，提供缓存策略、分页逻辑等.
 *
 * @author HuQingmiao
 */
@Service
public class TransactionService {
    private final static Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transRepository;

    /**
     * 缓存用户最近一次查询结果。这样,当用户来回翻页、反复点击查询时，系统就可以直接从缓存获取数据了 .
     */
    private final static Map<String, ArrayList<Transaction>> cache = new HashMap<>();

    /**
     * 缓存用户最近一次的查询条件, 因为用户会习惯性反复点击查询按钮, 如果查询条件与上次相同, 则直接取缓存.
     */
    private final static Map<String, TranscationQuery> lstQueryMap = new HashMap<>();


    public Transaction save(String userId, TransactionNewCmd transactionNewCmd) {
        Transaction t = transRepository.save(transactionNewCmd);
        cache.remove(userId);  // 当用户新增某条交易记录时, 要同时清空缓存. 否则, 翻页时, 不会显示当前新增的记录
        lstQueryMap.remove(userId);
        return t;
    }

    public void delete(String userId, String id) {
        boolean deleted = transRepository.delete(id);  // 根据id删除交易记录
        if (deleted) {
            cache.remove(userId);  // 当用户删除交易记录时, 要同时清空缓存. 否则, 翻页时, 仍会显示当前新增的记录
            lstQueryMap.remove(userId);
        }
    }

    public Transaction update(String userId, TransactionUpdCmd transactionUpdCmd) {
        Transaction t = transRepository.update(transactionUpdCmd);
        return t;
    }

    public Transaction queryById(String id) {
        return transRepository.findById(id);
    }

    /**
     * 分页查询，如果查询条件与上次相同，则从缓存中获取；如果查询条件发生变化, 则重新查询并缓存.
     */
    public PageList<Transaction> queryForPager(String userId, TranscationQuery query, int pageNo, int pageSize) {
        log.info(">> query transactions, userId={}, query={}, pageNo={}, pageSize={}", userId, query, pageNo, pageSize);

        TranscationQuery lastQuery = lstQueryMap.get(userId);
        if (lastQuery == null || !lastQuery.equals(query)) {  // 如果查询条件发生变化, 则重新查询
            // 缓存失效, 则册除缓存
            cache.remove(userId);
            lstQueryMap.remove(userId);

            // 重新查询，写入缓存
            switch (query.qryField().trim()) {
                case TranscationQuery.QRY_FIELD_ACCOUNT_NO:
                    log.info(" load data to cache,userId={}", userId);
                    cache.put(userId, transRepository.findByAccountNo(query.qryText(), query.transBeginTime(), query.transEndTime()));
                    break;
                case TranscationQuery.QRY_FIELD_TRANS_TYPE:
                    log.info(" load data to cache,userId={}", userId);
                    cache.put(userId, transRepository.findByTransType(query.qryText(), query.transBeginTime(), query.transEndTime()));
                    break;
                case TranscationQuery.QRY_FIELD_TRANS_CHANNEL:
                    log.info(" load data to cache,userId={}", userId);
                    cache.put(userId, transRepository.findByTransChannel(query.qryText(), query.transBeginTime(), query.transEndTime()));
                    break;
                default:
                    throw new IllegalArgumentException("无效的查询条件");
            }
        }
        lstQueryMap.put(userId, query);  // 缓存当前查询条件

        // 从缓存中获得数据
        log.info(" get from cache, userId={}", userId);
        ArrayList<Transaction> tList = cache.get(userId);
        if (tList == null) {
            return new PageList<>();
        }

        // 只返回当前页的数据
        return getPage(tList, pageNo, pageSize);
    }


    private static PageList<Transaction> getPage(ArrayList<Transaction> sourceList, int pageNo, int pageSize) {
        if (pageSize <= 0 || pageNo <= 0) {
            throw new IllegalArgumentException("无效的页数或每页大小");
        }

        int fromIndex = (pageNo - 1) * pageSize;
        if (sourceList == null || sourceList.size() <= fromIndex) {
            return new PageList<>();
        }

        // 从源数据中取出当前页的数据
        ArrayList<Transaction> tList = (ArrayList<Transaction>) sourceList.stream()
                .skip(fromIndex)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageList<>(sourceList.size(), tList);
    }

}


