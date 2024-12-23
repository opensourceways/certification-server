/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import java.util.Date;

import lombok.Data;

/**
 * 软件信息
 *
 * @since 2024/07/01
 */
@Data
public class Software {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业id
     */
    private Integer companyId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品功能介绍
     */
    private String productFunctionDesc;

    /**
     * 产品使用场景
     */
    private String usageScenesDesc;

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
     * 算力平台
     */
    private String jsonHashRatePlatform;

    /**
     * 服务器型号
     */
    private String serverType;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 测试机构
     */
    private String testOrganization;

    /**
     * 测试机构id
     */
    private Integer testOrgId;

    /**
     * 节点状态
     */
    private Integer status;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 申请时间
     */
    private Date applicationTime;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核权限
     */
    private Integer reviewRole;

    /**
     * 证书类型
     */
    private String certificateType;

    /**
     * 证书权益
     */
    private String certificateInterests;

    /**
     * 证书有效期
     */
    private String validityPeriod;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 公司编号
     */
    private Integer companyCode;

    /**
     * 证书编号
     */
    private String certificationId;


    /**
     * 认证状态
     */
    private String authenticationStatus;

    /**
     * CPU硬件厂商
     */
    private String cpuVendor;

    /**
     * 测评场景id
     */
    private Integer asId;

    /**
     * 认证完成时间
     */
    private Date certificationTime;

    /**
     * 删除标识
     */
    private Integer deleteFlag;
}
