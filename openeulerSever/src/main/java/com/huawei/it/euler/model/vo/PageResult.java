/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * ApprovalDataVo
 *
 * @since 2024/07/03
 */
@Data
public class PageResult<T> {
    private static final PageResult<?> EMPTY_PAGE = new PageResult<>();
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
    private JSONObject filterData;

    public PageResult() {
        this(Collections.emptyList(), 0, 1, 10);
    }

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public PageResult(List<T> list, long total, int pageNum, int pageSize, JSONObject filterData) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.filterData = filterData;
    }

    public static <T> PageResult<T> empty() {
        return (PageResult<T>) EMPTY_PAGE;
    }
}
