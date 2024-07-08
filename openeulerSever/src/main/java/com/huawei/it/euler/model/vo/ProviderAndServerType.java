/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

import java.util.List;

/**
 * ProviderAndServerType
 *
 * @since 2024/07/03
 */
@Data
public class ProviderAndServerType {
    private String serverProvider;

    private List<String> serverTypes;
}
