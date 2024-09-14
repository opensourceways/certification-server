/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


import java.util.Date;
import java.util.List;

/**
 * 软件信息
 *
 * @since 2024/07/01
 */
@Data
public class Software {
    private Integer id;

    @NotBlank(message = "公司名称不能为空")
    private String companyName;

    @JsonIgnore
    private Integer companyId;

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

    @JsonIgnore
    private String jsonHashRatePlatform;

    @NotBlank(message = "产品类型不能为空")
    @Length(max = 50, message = "产品类型最大不超过{max}个字符")
    private String productType;

    @NotBlank(message = "测试机构不能为空")
    @Length(max = 50, message = "测试机构最大不超过{max}个字符")
    private String testOrganization;

    private Integer testOrgId;

    private String cpuVendor;

    @JsonIgnore
    private Integer status;

    @JsonIgnore
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationTime;

    @JsonIgnore
    private String userTelephone;

    @JsonIgnore
    private String reviewer;

    @JsonIgnore
    private Integer reviewRole;

    @JsonIgnore
    private String userUuid;

    @JsonIgnore
    private Integer companyCode;

    private List<String> platforms;

    @JsonIgnore
    private String authenticationStatus;

    private Integer asId;
}
