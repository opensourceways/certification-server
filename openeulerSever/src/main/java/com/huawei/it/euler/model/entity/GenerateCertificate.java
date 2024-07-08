/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

/**
 * 生成证书
 *
 * @since 2024/07/01
 */
@Data
public class GenerateCertificate {
    /**
     * softwareId
     */
    private Integer id;

    /**
     * 公司名
     */
    private String companyName;

    /**
     * 产品名
     */
    private String productName;

    /**
     * 产品版本
     */
    private String productVersion;

    /**
     * 欧拉平台名
     */
    private String osName;

    /**
     * 欧拉版本
     */
    private String osVersion;

    /**
     * 算力平台
     */
    private String hashratePlatform;

    /**
     * 测试机构
     */
    private String testOrganization;

    /**
     * 有效期
     */
    private String validityPeriod;
}
