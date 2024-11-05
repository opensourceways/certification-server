/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.List;

@Tag(name = "批量新增业务数据响应对象", description = "用于返回批量新增业务数据结果")
@Data
public class BatchInsertResponse {

    private List<InsertResponse> results;

    @Schema(description = "新增成功数量")
    private int successCount;

    @Schema(description = "新增失败数量")
    private int failureCount;
}
