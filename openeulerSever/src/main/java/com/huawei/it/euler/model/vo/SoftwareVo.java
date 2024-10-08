/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * SoftwareVo
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareVo {

    private Integer id;

    private String productName;

    private String productVersion;

    private String osName;

    private String osVersion;

    private List<ComputingPlatformVo> hashratePlatformList;

    /**
     * 获取算力平台集合字符串
     */
    private String hashratePlatform;
    /**
     * 算力平台名称
     */
    private String hashratePlatformNameList;
    private List<String> platforms;
    private String jsonHashRatePlatform;

    private String reviewer;

    private Integer reviewRole;

    private Integer status;

    private Date updateTime;

    private Integer testOrgId;

    private String authenticationStatus;
}
