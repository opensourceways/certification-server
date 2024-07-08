/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

/**
 * CompatibleDataInfoVo
 *
 * @since 2024/07/04
 */
@Data
public class CompatibleDataInfoVo {
    private Integer dataId;

    private String productName;

    private String uploadCompany;

    private String createdBy;

    private String companyName;

    private String productVersion;

    private String systemName;

    private String systemVersion;

    private String serverPlatform;

    private String serverSupplier;

    private String serverModel;

    private String productType;

    private String status;

    private String operation;
}
