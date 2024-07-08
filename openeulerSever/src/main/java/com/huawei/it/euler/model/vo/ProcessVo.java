/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * ProcessVo
 *
 * @since 2024/07/03
 */
@Data
public class ProcessVo {
    @NotNull(message = "认证id不能为空")
    private Integer softwareId;

    @NotNull(message = "处理结果不能为空")
    private Integer handlerResult;

    @NotEmpty(message = "请输入审核意见")
    @Length(max = 1000, message = "审核意见最大不超过{max}个字符")
    private String transferredComments;

    @Length(max = 255, message = "转审人uuid错误")
    private String transferredUser;
}
