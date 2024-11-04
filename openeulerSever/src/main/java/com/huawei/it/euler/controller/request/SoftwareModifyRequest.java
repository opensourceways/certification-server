/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.controller.request;

import java.util.List;

import com.huawei.it.euler.model.vo.ComputingPlatformVo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SoftwareModifyRequest {

    @NotNull(message = "认证id不能为空")
    private Integer id;

    @NotNull(message = "产品名称不能为空")
    private String productName;

    @NotNull(message = "产品版本不能为空")
    private String productVersion;

    @NotNull(message = "os名称不能为空")
    private String osName;

    @NotNull(message = "os版本不能为空")
    private String osVersion;

    @NotNull(message = "算力平台不能为空")
    private List<@Valid ComputingPlatformVo> hashratePlatformList;
}
