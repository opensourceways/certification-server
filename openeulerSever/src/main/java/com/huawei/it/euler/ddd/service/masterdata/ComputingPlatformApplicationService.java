/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;

import java.util.List;

/**
 * 算力平台 application接口
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
public interface ComputingPlatformApplicationService {
    public ComputingPlatform add(ComputingPlatformCommand command);

    public void delete(ComputingPlatformCommand command);

    public List<ComputingPlatform> findList(ComputingPlatformQuery query);
}
