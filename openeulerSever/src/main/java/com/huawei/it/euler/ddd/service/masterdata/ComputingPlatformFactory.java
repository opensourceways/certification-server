/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 算力平台 工厂类
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
@Component
public class ComputingPlatformFactory {
    public ComputingPlatform toComputingPlatform(ComputingPlatformCommand command) {
        ComputingPlatform computingPlatform = new ComputingPlatform();
        BeanUtils.copyProperties(command, computingPlatform);
        return computingPlatform;
    }

    public ComputingPlatformQuery toQuery(ComputingPlatformCommand command) {
        ComputingPlatformQuery query = new ComputingPlatformQuery();
        query.setPlatformName(command.getPlatformName());
        query.setServerProvider(command.getServerProvider());
        query.setServerType(command.getServerType());
        return query;
    }
}
