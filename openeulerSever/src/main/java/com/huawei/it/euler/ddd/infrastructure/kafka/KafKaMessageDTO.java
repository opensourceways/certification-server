/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.kafka;

import lombok.Data;

import java.util.Date;

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
     * 待办消息id
     */
    private Integer todoId;

    /**
     * 待办消息状态
     */
    private String todoStatus;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 跳转链接
     */
    private String redirectUrl;

    /**
     * 创建时间
     */
    private Date createTime;

}
