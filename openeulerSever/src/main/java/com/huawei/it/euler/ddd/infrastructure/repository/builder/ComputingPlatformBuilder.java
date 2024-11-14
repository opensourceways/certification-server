/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.builder;

import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.ComputingPlatformPO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 算例平台构造器
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
@Component
public class ComputingPlatformBuilder {
    public ComputingPlatformPO fromComputingPlatform(ComputingPlatform computingPlatform) {
        ComputingPlatformPO computingPlatformPO = new ComputingPlatformPO();
        BeanUtils.copyProperties(computingPlatform, computingPlatformPO);
        return computingPlatformPO;
    }

    public ComputingPlatform toComputingPlatform(ComputingPlatformPO computingPlatformPO) {
        ComputingPlatform computingPlatform = new ComputingPlatform();
        BeanUtils.copyProperties(computingPlatformPO, computingPlatform);
        return computingPlatform;
    }

    public List<ComputingPlatform> toComputingPlatformList(List<ComputingPlatformPO> computingPlatformPOList) {
        return computingPlatformPOList.stream().map(this::toComputingPlatform).toList();
    }
}
