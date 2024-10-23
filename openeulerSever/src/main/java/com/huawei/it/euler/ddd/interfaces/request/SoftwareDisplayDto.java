/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.request;

import java.util.List;

import lombok.Data;

@Data
public class SoftwareDisplayDto {
    private int softwareId;

    private String productName;

    private List<Integer> idList;
}
