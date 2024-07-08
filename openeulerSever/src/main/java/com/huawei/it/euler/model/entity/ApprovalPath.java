/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * ApprovalPath
 *
 * @since 2024/07/03
 */
@Data
public class ApprovalPath {
    private Integer id;

    private Integer icId;

    private Integer planReview;

    private Integer reportFirstTrial;

    private Integer reportReexamination;

    private Integer certFirstTrial;

    private Integer certReexamination;

    private Date updateTime;
}
