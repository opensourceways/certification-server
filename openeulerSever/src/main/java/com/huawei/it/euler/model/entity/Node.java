/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * Node
 *
 * @since 2024/07/04
 */
@Data
public class Node {
    private Integer id;

    private Integer softwareId;

    private String nodeName;

    private String handler;

    private Integer status;

    private Date handlerTime;

    private Integer handlerResult;

    private String transferredComments;

    private Date updateTime;
}
