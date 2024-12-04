/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 自定义分页对象
 *
 * @author zhaoyan
 * @since 2024-12-03
 */
public class PageResult<T> extends Page<T> {
    private JSONObject filterData;

    public JSONObject getFilterData() {
        return filterData;
    }

    public void setFilterData(JSONObject filterData) {
        this.filterData = filterData;
    }
}
