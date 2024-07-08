/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * AuditRecordsVo
 *
 * @since 2024/07/01
 */
@Data
public class AuditRecordsVo {
    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理人名字
     */
    private String handlerName;

    /**
     * 处理结果
     */
    private String handlerResult;

    /**
     * 处理时间
     */
    private String handlerTime;

    /**
     * 审核意见
     */
    private String transferredComments;

    /**
     * 节点状态
     */
    @JsonIgnore
    private Integer status;
}
