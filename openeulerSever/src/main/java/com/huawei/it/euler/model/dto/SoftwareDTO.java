/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import lombok.Data;

import java.util.List;

/**
 * SoftwareDTO
 *
 * @since 2024/07/01
 */
@Data
@ExcelIgnoreUnannotated
public class SoftwareDTO {
    /**
     * 软件id
     */
    @ExcelProperty("序号")
    private Integer id;

    /**
     * 产品名称
     */
    @ExcelProperty("产品名称")
    private String productName;

    /**
     * 公司名称
     */
    @ExcelProperty("企业名称")
    private String companyName;

    /**
     * 产品类型
     */
    @ExcelProperty("测评类型")
    private String productType;

    /**
     * 产品版本
     */
    @ExcelProperty("测评版本")
    private String productVersion;

    /**
     * os名称
     */
    @ExcelProperty("os名称")
    private String osName;

    /**
     * os版本
     */
    @ExcelProperty("os版本")
    private String osVersion;

    /**
     * 产品功能介绍
     */
    @ExcelProperty("产品功能介绍")
    private String productFunctionDesc;

    /**
     * 产品使用场景
     */
    @ExcelProperty("产品使用场景")
    private String usageScenesDesc;

    /**
     * 算力平台集合
     */
    private List<ComputingPlatformVo> hashratePlatformList;

    /**
     * 测试机构
     */
    @ExcelProperty("测试机构")
    private String testOrganization;

    private Integer testOrgId;

    /**
     * 算力平台名称
     */
    @ExcelProperty("算力平台名称")
    private String hashratePlatformNameList;

    /**
     * 平台集合字符串
     */
    @ExcelProperty("算力平台详情")
    private String platformsList;

    /**
     * 申请人姓名
     */
    @ExcelProperty("申请人姓名")
    private String applicantName;

    /**
     * 申请时间
     */
    @ExcelProperty("申请时间")
    private String applicationTime;

    /**
     * 认证完成时间
     */
    @ExcelProperty("认证完成时间")
    private String certificationTime;

    /**
     * 证书有效期
     */
    @ExcelProperty("证书有效期")
    private String validityPeriod;

    /**
     * 证书编号
     */
    @ExcelProperty("证书名称")
    private String certificateId;

    private String applicant;

    private String reviewer;

    private String preReviewer;
    /**
     * 审核人姓名
     */
    @ExcelProperty("审核人姓名")
    private String reviewerName;

    /**
     * 认证状态
     */

    private Integer status;

    @ExcelProperty("认证状态")
    private String statusName;
    /**
     * 平台集合
     */
    private List<String> platforms;

    /**
     * 硬件厂商
     */
    private String cpuVendor;
}
