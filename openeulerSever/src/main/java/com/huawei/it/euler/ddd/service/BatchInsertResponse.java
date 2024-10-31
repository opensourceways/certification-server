/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import lombok.Data;

import java.util.List;

@Data
public class BatchInsertResponse {
    private List<InsertResponse> results;

    private int successCount;

    private int failureCount;
}
