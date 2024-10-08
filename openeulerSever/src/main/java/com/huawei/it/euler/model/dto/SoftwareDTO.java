/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.dto;

import java.util.List;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;

import lombok.Data;

/**
 * SoftwareVo
 *
 * @since 2024/07/01
 */
@Data
@ExcelIgnoreUnannotated
public class SoftwareDTO {
    /**
     * 软件id
     */
    @ExcelProperty("流程序号")
    private Integer id;

    /**
     * 公司名称
     */
    @ExcelProperty("公司名称")
    private String companyName;

    /**
     * 产品名称
     */
    @ExcelProperty("产品名称")
    private String productName;

    /**
     * 产品类型
     */
    @ExcelProperty("产品类型")
    private String productType;

    /**
     * 产品版本
     */
    @ExcelProperty("产品版本")
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
     * 算力平台集合
     */
    private List<ComputingPlatformVo> hashratePlatformList;

    /**
     * 测试机构
     */
    @ExcelProperty("测试机构")
    private String testOrganization;

    /**
     * 申请时间
     */
    @ExcelProperty("申请时间")
    private String applicationTime;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    /**
     * 认证状态
     */
    @ExcelProperty("认证状态")
    private String status;

    /**
     * 算力平台名称
     */
    @ExcelProperty("算力平台名称")
    private String hashratePlatformaNameList;

    /**
     * 获取算力平台集合字符串
     */
    private String hashratePlatform;

    /**
     * 硬件厂商
     */
    @ExcelProperty("硬件厂商")
    private String cpuVendor;

    /**
     * 认证完成时间
     */
    @ExcelProperty("认证完成时间")
    private String certificationTime;
}
