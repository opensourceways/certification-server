/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


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
