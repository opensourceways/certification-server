/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * CertificateConfirmVo
 *
 * @since 2024/07/03
 */
@Data
public class CertificateConfirmVo {
    @NotNull(message = "软件id不能为空")
    private Integer softwareId;

    @NotBlank(message = "产品版本不能为空")
    @Length(max = 64, message = "产品版本长度最大为64")
    private String productVersion;

    @NotBlank(message = "os名称不能为空")
    @Length(max = 50, message = "os名称最大不超过{max}个字符")
    private String osName;

    @NotBlank(message = "os版本不能为空")
    @Length(max = 50, message = "os版本最大不超过{max}个字符")
    private String osVersion;

    @NotNull(message = "算力平台不能为空")
    private List<@Valid ComputingPlatformVo> hashratePlatformList;
}
