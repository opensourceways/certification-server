/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ComputingPlatformVo
 *
 * @since 2024/07/01
 */
@Data
public class ComputingPlatformVo {
    @NotBlank(message = "算力平台不能为空")
    @Length(max = 50, message = "算力平台最大不超过{max}个字符")
    private String platformName;

    @NotBlank(message = "硬件厂家不能为空")
    @Length(max = 50, message = "硬件厂家最大不超过{max}个字符")
    private String serverProvider;

    @NotNull(message = "硬件型号不能为空")
    private List<@Length(max = 50, message = "硬件型号最大不超过{max}个字符") String> serverTypes;
}
