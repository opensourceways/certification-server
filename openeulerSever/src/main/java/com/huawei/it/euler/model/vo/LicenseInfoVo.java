/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

/**
 * LicenseInfoVo
 *
 * @since 2024/07/04
 */
@Data
public class LicenseInfoVo {
    private String companyName;

    private String creditCode;

    private String address;

    private String legalPerson;

    private String registrationCapital;

    private String registrationDate;

    private String expirationDate;
}
