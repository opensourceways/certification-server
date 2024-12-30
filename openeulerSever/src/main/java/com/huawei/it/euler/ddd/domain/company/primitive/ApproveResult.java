/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.company.primitive;

import lombok.Getter;

/**
 * 企业审核结果枚举
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Getter
public enum ApproveResult {

    PASS(true,"通过"),

    REJECT(false,"驳回");

    private final Boolean value;

    private final String desc;

    ApproveResult(Boolean value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String findDesc(Boolean value){
        return value? PASS.getDesc() : REJECT.getDesc();
    }
}