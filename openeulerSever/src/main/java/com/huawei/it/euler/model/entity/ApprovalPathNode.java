/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * ApprovalPathNode
 *
 * @since 2024/07/03
 */
@Data
public class ApprovalPathNode {
    private Integer id;

    private Integer icId;

    private Integer roleId;

    private Integer softwareStatus;

    private String approvalNodeName;

    private String userTelephone;

    private String username;

    private String userUuid;

    private Date updateTime;
}
