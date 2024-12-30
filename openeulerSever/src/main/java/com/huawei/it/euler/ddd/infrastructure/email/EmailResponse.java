/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.email;

import lombok.Data;

/**
 * 邮件发送响应对象
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Data
public class EmailResponse {
    /**
     * 邮箱发送请求成功结果码，不等于该值则为发送失败
     */
    private final static String SUCCESS_CODE = "000000";

    /**
     * 邮箱发送服务请求无响应：结果码
     */
    private final static String SEND_EXCEPTION_CODE = "200000";


    /**
     * 结果码
     */
    private String code;

    /**
     * 返回信息
     */
    private String description;

    public boolean isSuccess(){
        return SUCCESS_CODE.equals(code);
    }

    public static EmailResponse sendSuccess(){
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setCode(SUCCESS_CODE);
        return emailResponse;
    }

    public static EmailResponse sendExceptionResponse(String exceptionMsg){
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setCode(SEND_EXCEPTION_CODE);
        emailResponse.setDescription(exceptionMsg);
        return emailResponse;
    }
}