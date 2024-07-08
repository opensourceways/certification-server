/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.List;

/**
 * SelectSoftware
 *
 * @since 2024/07/01
 */
@Data
public class SelectSoftware {
    /**
     * 产品名称
     */
    private String productName;

    /**
     * 认证状态
     */
    private List<String> status;

    /**
     * 测试机构
     */
    private List<String> testOrganization;

    /**
     * 产品类型
     */
    private List<String> productType;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 申请人/审核人
     */
    private String applicant;
}
