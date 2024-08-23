/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Conpatibility
 *
 * @since 2024/08/23
 */
@Data
@Accessors(chain = true)
public class CompatibilityVo {
    private Integer certId;

    private Integer dataId;

    private String type;

    private String testOrganization;

    private String productName;

    private String productVersion;

    private String companyName;

    private List<ComputingPlatformVo> platformTypeAndServerModel;

    private String authenticateLink;

    private String osName;

    private String osVersion;

    private String region;
}
