/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * CertificateInfo
 *
 * @since 2024/07/01
 */
@Data
public class CertificateInfoVo {
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
