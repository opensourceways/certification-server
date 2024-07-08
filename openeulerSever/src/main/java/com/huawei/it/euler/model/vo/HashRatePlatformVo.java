/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

import java.util.List;

/**
 * HashRatePlatformVo
 *
 * @since 2024/07/03
 */
@Data
public class HashRatePlatformVo {
    private String platformName;

    private List<ProviderAndServerType> providerAndServerTypes;
}
