/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;

import java.util.List;

/**
 * 算力平台 持久化接口
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
public interface ComputingPlatformRepository {

    public ComputingPlatform add(ComputingPlatform computingPlatform);

    public void delete(Integer id);

    public List<ComputingPlatform> findList(ComputingPlatformQuery query);

}
