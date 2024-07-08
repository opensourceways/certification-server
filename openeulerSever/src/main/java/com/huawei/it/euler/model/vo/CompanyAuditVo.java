/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * CompanyAuditVo
 *
 * @since 2024/07/03
 */
@Data
public class CompanyAuditVo {
    @NotEmpty(message = "用户uuid不能为空")
    private String userUuid;

    @NotNull(message = "审批结果不能为空")
    private Boolean result;

    @NotNull(message = "审核意见不能为空")
    @Length(max = 1000, message = "评审意见超长")
    private String comment;
}
