package com.huawei.it.euler.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    /**
     * total page count
     */
    private Integer totalNum;

    /**
     * data
     */
    private List<T> data;

    public PageVo(Integer totalNum, List<T> data) {
        this.totalNum = totalNum;
        this.data = data;
    }
}
