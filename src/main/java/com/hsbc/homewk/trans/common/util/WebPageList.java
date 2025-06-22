package com.hsbc.homewk.trans.common.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 专门为前端分页查询定义的查询结果类。它包含当前面的数据，总的记录数，总的页数，当前页码等web页面需要的信息。
 *
 * @author HuQingmiao
 */
public class WebPageList<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    // 查询得到的当前面的数据
    private ArrayList<E> dataList;

    // 符合条件的总的记录数
    private int totalCount;

    // 符合条件的总的页数
    private int totalPages;

    // 当前页码
    private int currPageNo;

    // 每页显示的记录数
    private int pageSize;


    public WebPageList() {
        super();
        this.dataList = new ArrayList<>();
    }

    public WebPageList(int totalCount, ArrayList<E> dataList, int currPageNo, int pageSize) {
        this.totalCount = totalCount;
        this.totalPages = (totalCount + pageSize - 1) / pageSize;

        this.dataList = dataList;
        this.currPageNo = currPageNo;
        this.pageSize = pageSize;
    }


    public ArrayList<E> getDataList() {
        return dataList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrPageNo() {
        return currPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

}

