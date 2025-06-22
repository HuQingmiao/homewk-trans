package com.hsbc.homewk.trans.service;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简化版基于内存的ID生成器, 算法：时间(yyyyMMddHHmmss) + 5位递增顺序号(00000-99999)
 *
 * @author HuQingmiao
 */
public class IdGenerator {
    // 顺序号部分(5位数字，范围00000-99999)
    private static final AtomicInteger sequence = new AtomicInteger(0);

    // 时间格式
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    /**
     * 生成下一个ID
     *
     * @return 生成的ID字符串
     */
    public static String nextId() {
        // 时间部分
        String timePart = TIME_FORMATTER.format(Instant.now());

        // 序列号部分(5位数字，前面补零)
        int seq = sequence.getAndUpdate(prev -> (prev + 1) % 100000);
        String seqPart = String.format("%05d", seq);

        return timePart + seqPart;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            System.out.println(nextId());
        }
    }
}