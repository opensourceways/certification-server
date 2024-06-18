/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

/**
 * ApprovalPath
 *
 * @since 2024/07/03
 */
@Data
public class ApprovalScenario {
    private Integer id;

    private Integer icId;

    private String name;

    private String description;

    private String conditions;
}
