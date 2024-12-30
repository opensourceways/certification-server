/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.sms;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * sms短信配置参数
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Validated
@Component
@Configuration
@ConfigurationProperties(prefix = "sns")
@Data
public class SmsProperties {

    /**
     * 应用请求地址
     */
    @NotNull
    private String messageUrl;

    /**
     * 应用key
     */
    @NotNull
    private String appKey;

    /**
     * 应用密码
     */
    @NotNull
    private String appSecret;

    /**
     * 签名渠道
     */
    @NotNull
    private String senderId;

    /**
     * 企业审核通知模板
     */
    @NotNull
    private String templateId;

    /**
     * 测评业务进度通知模板
     */
    @NotNull
    private String progressNoticeTemplateId;
}
