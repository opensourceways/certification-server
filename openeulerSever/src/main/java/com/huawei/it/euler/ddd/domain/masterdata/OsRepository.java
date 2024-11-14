/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;

import java.util.List;

/**
 * 操作系统 持久化接口
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
public interface OsRepository {
    public Os add(Os os);

    public void delete(Integer id);

    public List<Os> findList(OsQuery query);
}
