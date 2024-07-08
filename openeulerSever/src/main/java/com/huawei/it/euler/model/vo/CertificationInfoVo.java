/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import shade.fasterxml.jackson.annotation.JsonIgnore;

/**
 * CertificationInfoVo
 *
 * @since 2024/07/01
 */
@Data
public class CertificationInfoVo {
    /**
     * 证书类型
     */
    private String certificateType;

    /**
     * 证书权益
     */
    private String certificateInterests;

    /**
     * 有效期
     */
    private String validityPeriod;

    /**
     * 软件id
     */
    @JsonIgnore
    private Integer softwareId;
}
