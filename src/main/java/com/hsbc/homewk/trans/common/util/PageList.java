package com.hsbc.homewk.trans.common.util;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 分页查询结果封装类
 */
public class PageList<E> implements Serializable {
    // 查询得到的当前面的数据
    private ArrayList<E> dataList;

    // 符合条件的总的记录数
    private int totalCount;

    public PageList() {
        super();
        this.dataList = new ArrayList<>();
    }

    public PageList(int totalCount, ArrayList<E> dataList) {
        this.totalCount = totalCount;
        this.dataList = dataList;
    }

    public ArrayList<E> getDataList() {
        return dataList;
    }

    public int getTotalCount() {
        return totalCount;
    }
}

