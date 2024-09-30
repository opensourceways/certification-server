/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * SelectSoftware
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareQuery {
    /**
     * 产品名称
     */
    private String productName;

    /**
     * 认证状态
     */
    private List<String> status;

    private List<Integer> statusId;
    /**
     * 测试机构
     */
    private List<String> testOrganization;

    /**
     *  测试机构id
     */
    private List<Integer> testOrgId;

    /**
     * 权限范围
     */
    private String dataScope;

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

    /**
     * 当前用户uuid
     */
    private String uuid;

    /**
     * 认证完成时间筛选-开始时间
     */
    private Date beginCertificationTime;

    /**
     * 认证完成时间筛选-结束时间
     */
    private Date endCertificationTime;

    /**
     * 排序
     */
    private String sort;
}
