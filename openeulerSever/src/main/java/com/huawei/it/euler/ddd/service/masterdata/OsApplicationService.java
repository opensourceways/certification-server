/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;

import java.util.List;

/**
 * 操作系统 application接口
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
public interface OsApplicationService {
    public Os add(OsCommand command);

    public void delete(OsCommand command);

    public List<Os> findList(OsQuery query);
}
