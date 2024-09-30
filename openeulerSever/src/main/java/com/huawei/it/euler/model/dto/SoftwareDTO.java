/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.dto;

import java.util.Date;
import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * SoftwareVo
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareDTO {
    @ExcelProperty("字符串标题")
    @NotNull(message = "认证id不能为空")
    private Integer id;

    @ExcelProperty("字符串标题")
    @NotNull(message = "产品名称不能为空")
    private String productName;

    @ExcelProperty("字符串标题")
    @NotNull(message = "产品版本不能为空")
    private String productVersion;

    @ExcelProperty("字符串标题")
    @NotNull(message = "os名称不能为空")
    private String osName;

    @ExcelProperty("字符串标题")
    @NotNull(message = "os版本不能为空")
    private String osVersion;

    @ExcelProperty("字符串标题")
    @NotNull(message = "算力平台不能为空")
    private List<@Valid ComputingPlatformVo> hashratePlatformList;

    @JsonIgnore
    private String jsonHashRatePlatform;

    @JsonIgnore
    private String reviewer;

    @JsonIgnore
    private Integer reviewRole;

    @JsonIgnore
    private Integer status;

    @JsonIgnore
    private Date updateTime;

    private Integer testOrgId;
    @JsonIgnore
    private String authenticationStatus;
}
