/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * SoftwareVo
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareVo {
    @NotNull(message = "认证id不能为空")
    private Integer id;

    @NotNull(message = "产品版本不能为空")
    private String productVersion;

    @NotNull(message = "os名称不能为空")
    private String osName;

    @NotNull(message = "os版本不能为空")
    private String osVersion;

    @NotNull(message = "算力平台不能为空")
    private List<@Valid ComputingPlatformVo> hashratePlatformList;

    @JsonIgnore
    private String jsonHashRatePlatform;

    @JsonIgnore
    private String reviewer;

    @JsonIgnore
    private Integer status;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private String authenticationStatus;
}
