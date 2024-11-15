/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import lombok.Data;

import java.util.Date;

/**
 * 注释
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Data
public class Command {

    private String lastUpdatedBy;

    private Date lastUpdatedTime;

    public interface Add {}

    public interface Update {}
}
