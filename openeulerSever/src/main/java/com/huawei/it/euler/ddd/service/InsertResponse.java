/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Tag(name = "新增业务数据响应对象", description = "用于返回新增业务数据结果")
@Data
public class InsertResponse {

    @Schema(description = "业务唯一标识")
    private String unique;

    @Schema(description = "是否新增成功")
    private boolean success;

    @Schema(description = "响应消息")
    private String message;
}
