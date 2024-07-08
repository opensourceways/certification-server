/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * ApprovalPathVo
 *
 * @since 2024/07/03
 */
@Data
public class ApprovalPathVo {
    private String innovationCenterName;

    private String planReview;

    private String reportFirstTrial;

    private String reportReexamination;

    private String certFirstTrail;

    private String certTeexamination;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date updateTime;
}
