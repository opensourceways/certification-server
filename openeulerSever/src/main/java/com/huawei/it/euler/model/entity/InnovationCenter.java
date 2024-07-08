/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * InnovationCenter
 *
 * @since 2024/07/03
 */
@Data
public class InnovationCenter {
    private Integer id;

    private String name;

    private String type;

    private Date updateTime;
}
