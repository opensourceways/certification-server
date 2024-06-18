/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 兼容性数据
 *
 * @since 2024/07/01
 */
@Data
@Accessors(chain = true)
public class CompatibleDataInfo {
    /**
     * id
     */
    private Integer id;

    /**
     * 数据id
     */
    private Integer dataId;

    /**
     * 上传公司
     */
    private String uploadCompany;

    /**
     * 上传人
     */
    private String createdBy;

    /**
     * 上传人唯一标识uuid
     */
    private String uuid;

    /**
     * ISV公司名称
     */
    private String companyName;

    /**
     * 测试产品名称
     */
    private String productName;

    /**
     * 测试产品版本号
     */
    private String productVersion;

    /**
     * 产品功能介绍
     */
    private String productFunctions;

    /**
     * 使用场景介绍
     */
    private String usageScene;

    /**
     * 操作系统名称
     */
    private String systemName;

    /**
     * 操作系统版本号
     */
    private String systemVersion;

    /**
     * 服务器算力平台
     */
    private String serverPlatform;

    /**
     * 服务器厂商
     */
    private String serverSupplier;

    /**
     * 服务器型号
     */
    private String serverModel;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品子类型
     */
    private String productSubtype;

    /**
     * 申请区域
     */
    private String region;

    /**
     * 发证时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date issuanceDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
