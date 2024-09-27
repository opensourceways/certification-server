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
    //用户无权限
    UNAUTHORIZED( "该用户无权访问当前信息"),
    // 企业未认证
    COMPANY_NOT_APPROVED( "申请兼容性测评服务需要完成企业实名认证"),
    // 未签署协议
    PROTOCOL_NOT_SIGNED("申请兼容性测评服务需要签署技术测评协议"),
    // 审批流程不存在
    APPROVAL_PROCESS_NOT_EXIST( "审批流程不存在"),
    // 用户无操作权限
    UNAUTHORIZED_OPERATION( "用户无操作权限"),
    //审批流程状态错误
    APPROVAL_PROCESS_STATUS_ERROR( "审批流程状态错误"),
    //审批流程参数不合法
    INVALID_PARAMETERS( "审批流程参数不合法"),
    //测试报告不存在
    FILE_NOT_EXIST( "测试报告不存在"),
    //当前流程已经撤回
    CANCELLED( "当前流程已经撤回"),
    //转审人不能为自己
    CANNOT_BE_SELF( "转审人不能为自己"),
    ;


    private final String message;

    ErrorCodes( String message) {
        this.message = message;
    }
}
