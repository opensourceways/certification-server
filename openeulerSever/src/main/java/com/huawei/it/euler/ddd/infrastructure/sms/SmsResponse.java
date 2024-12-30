/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.sms;

import lombok.Data;

/**
 * 短信发送响应
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Data
public class SmsResponse {
    /**
     * 华为为短信发送请求成功结果码，不等于该值则为发送失败
     */
    private final static String SUCCESS_CODE = "000000";

    /**
     * 华为云短信参数异常：结果码
     */
    private final static String INVALID_PARAMETER_CODE = "100000";

    /**
     * 华为云短信参数异常：信息
     */
    private final static String INVALID_PARAMETER_DESC = "body is null.";

    /**
     * 华为云短信发送服务请求无响应：结果码
     */
    private final static String SEND_EXCEPTION_CODE = "200000";

    /**
     * 华为云短信发送服务请求无响应：结果码
     */
    private final static String NO_RESPONSE_CODE = "300000";

    /**
     * 华为云短信发送服务请求无响应：信息
     */
    private final static String NO_RESPONSE_DESC = "sms service no response!";

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

    public static SmsResponse invalidParameterResponse(){
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setCode(INVALID_PARAMETER_CODE);
        smsResponse.setDescription(INVALID_PARAMETER_DESC);
        return smsResponse;
    }

    public static SmsResponse sendExceptionResponse(String exceptionMsg){
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setCode(SEND_EXCEPTION_CODE);
        smsResponse.setDescription(exceptionMsg);
        return smsResponse;
    }

    public static SmsResponse noResponse(){
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setCode(NO_RESPONSE_CODE);
        smsResponse.setDescription(NO_RESPONSE_DESC);
        return smsResponse;
    }

}
