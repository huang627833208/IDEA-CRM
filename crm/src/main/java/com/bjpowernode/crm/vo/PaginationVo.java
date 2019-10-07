package com.bjpowernode.crm.vo;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;

/**
 * 作者：杜聚宾
 */
public class PaginationVo<T> {

    private int total;
    private List<T> dataList;

    public int getTotal() {
        return total;
    }

    public PaginationVo<T> setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public PaginationVo<T> setDataList(List<T> dataList) {
        this.dataList = dataList;
        return this;
    }
}
