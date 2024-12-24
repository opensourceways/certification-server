/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.kafka;

import lombok.Data;

/**
 * kafka消息DTO
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
@Data
public class KafKaMessageDTO {

    /**
     * 消息接收人
     */
    private String user;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 跳转链接
     */
    private String redirectUrl;

}
