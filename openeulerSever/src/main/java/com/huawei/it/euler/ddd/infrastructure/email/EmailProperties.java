/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.email;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 邮箱属性
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Validated
@Component
@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailProperties {
    /**
     * 邮箱主机
     */
    @NotNull
    private String host;

    /**
     * 端口
     */
    @NotNull
    private String port;

    /**
     * 发件人用户名
     */
    @NotNull
    private String userName;

    /**
     * 发件人密码
     */
    @NotNull
    private String pwd;
}