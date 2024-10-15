/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.controller.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.huawei.it.euler.model.vo.ComputingPlatformVo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SoftwareCreateRequest {

    private Integer id;

    @NotBlank(message = "产品名称不能为空")
    @Length(max = 64, message = "产品名称最大不超过{max}个字符")
    private String productName;

    @NotBlank(message = "产品功能介绍不能为空")
    @Length(max = 1000, message = "产品功能介绍最大不超过{max}个字符")
    private String productFunctionDesc;

    @NotBlank(message = "使用场景介绍不能为空")
    @Length(max = 1000, message = "使用场景介绍最大不超过{max}个字符")
    private String usageScenesDesc;

    @NotBlank(message = "产品版本不能为空")
    @Length(max = 64, message = "产品版本最大不超过{max}个字符")
    private String productVersion;

    @NotBlank(message = "os名称不能为空")
    @Length(max = 50, message = "os名称最大不超过{max}个字符")
    private String osName;

    @NotBlank(message = "os版本不能为空")
    @Length(max = 50, message = "os版本最大不超过{max}个字符")
    private String osVersion;

    @NotNull(message = "算力平台不能为空")
    private List<@Valid ComputingPlatformVo> hashratePlatformList;

    @NotBlank(message = "产品类型不能为空")
    @Length(max = 50, message = "产品类型最大不超过{max}个字符")
    private String productType;

    @NotBlank(message = "测试机构不能为空")
    @Length(max = 50, message = "测试机构最大不超过{max}个字符")
    private String testOrganization;

    private String cpuVendor;
}
