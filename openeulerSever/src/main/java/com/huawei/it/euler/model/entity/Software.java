/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import java.util.Date;
import java.util.List;

import com.huawei.it.euler.model.vo.ComputingPlatformVo;

import lombok.Data;

/**
 * 软件信息
 *
 * @since 2024/07/01
 */
@Data
public class Software {

    private Integer id;

    private String companyName;

    private Integer companyId;

    private String productName;

    private String productFunctionDesc;

    private String usageScenesDesc;

    private String productVersion;

    private String osName;

    private String osVersion;

    private List<ComputingPlatformVo> hashratePlatformList;

    private String jsonHashRatePlatform;

    private String productType;

    private String testOrganization;

    private Integer testOrgId;

    private String cpuVendor;

    private Integer status;

    private Date updateTime;

    private Date applicationTime;

    private String userTelephone;

    private String reviewer;

    private Integer reviewRole;

    private String userUuid;

    private Integer companyCode;

    private List<String> platforms;

    private String authenticationStatus;

    private Integer asId;

    private Date certificationTime;
}
