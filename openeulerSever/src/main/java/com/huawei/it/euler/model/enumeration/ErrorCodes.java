/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import lombok.Getter;

/**
 * 公共错误码枚举类
 */
@Getter
public enum ErrorCodes {
    // 用户无权限
    UNAUTHORIZED( "该用户无权访问当前信息"),
    // 企业未认证
    COMPANY_NOT_APPROVED( "申请兼容性测评服务需要完成企业实名认证"),
    // 未签署协议
    PROTOCOL_NOT_SIGNED("申请兼容性测评服务需要签署技术测评协议");


    private final String message;

    ErrorCodes( String message) {
        this.message = message;
    }
}