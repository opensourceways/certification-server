/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import lombok.Data;

@Data
public class InsertResponse {
    private String unique;

    private boolean success;

    private String message;
}
