/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class FilterCriteriaVo {

    /**
     * 操作系统
     */
    private List<String> osNames;

    /**
     * 测试机构
     */
    private List<String> testOrganizations;
}
