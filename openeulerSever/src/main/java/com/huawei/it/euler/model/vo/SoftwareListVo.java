/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * SoftwareListVo
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareListVo {
    /**
     * 软件id
     */
    private Integer id;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品版本
     */
    private String productVersion;

    /**
     * os名称
     */
    private String osName;

    /**
     * os版本
     */
    private String osVersion;

    /**
     * 获取算力平台集合字符串
     */
    @JsonIgnore
    private String hashratePlatform;

    /**
     * 算力平台集合
     */
    private List<ComputingPlatformVo> hashratePlatformList;

    /**
     * 测试机构
     */
    private String testOrganization;

    /**
     * 测试机构ID
     */
    private Integer testOrgId;

    /**
     * 申请时间
     */
    private String applicationTime;

    /**
     * 申请人ID
     */
    private String applicant;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 审核人ID
     */
    private String reviewer;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    @JsonIgnore
    private Integer reviewRole;
    /**
     * 认证状态
     */
    private String status;
    /**
     * 算力平台名称
     */
    private String hashratePlatformaNameList;

    /**
     * 硬件厂商
     */
    private String cpuVendor;

    /**
     * 已驳回字段
     */
    @JsonIgnore
    private String authenticationStatus;

    /**
     * 操作
     */
    private String operation;

    /**
     * 测评场景id
     */
    private Integer asId;
}
