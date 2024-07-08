/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 协议
 *
 * @since 2024/06/29
 */
@Data
@Accessors(chain = true)
public class Protocol {
    private Integer id;

    /**
     * 协议类型
     * 1-隐私政策，2-技术测评协议，3-兼容性清单使用声明
     */
    private Integer protocolType;

    /**
     * 协议名称
     */
    private String protocolName;

    /**
     * 签署状态
     * 0-不同意，1-同意
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
