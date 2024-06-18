/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import lombok.Data;

import java.util.List;

/**
 * Conpatibility
 *
 * @since 2024/07/01
 */
@Data
public class Compatibility {
    private Integer certId;

    private String type;

    private String testOrganization;

    private String productName;

    private String productVersion;

    private String companyName;

    private String jsonPlatformTypeAndServerModel;

    private List<ComputingPlatformVo> platformTypeAndServerModel;

    private String authenticateLink;

    private String osName;

    private String osVersion;
}
