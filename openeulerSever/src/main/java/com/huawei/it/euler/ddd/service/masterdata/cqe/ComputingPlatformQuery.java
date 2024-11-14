/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.cqe;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-13
 */
@Tag(name = "算力平台查询对象", description = "用于存储查询命令字段数据")
@Data
public class ComputingPlatformQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "算力平台id")
    private Integer id;

    @Schema(description = "算力平台")
    private String platformName;

    @Schema(description = "服务厂家")
    private String serverProvider;

    @Schema(description = "服务器类型")
    private String serverType;

    @Schema(description = "正序排序字段")
    private List<String> ascSort;

    @Schema(description = "倒序排序字段")
    private List<String> descSort;

    @Schema(description = "请求页码")
    @NotNull(message = "请求页码不能为空")
    private int current;

    @Schema(description = "请求页数量")
    @NotNull(message = "请求页数量不能为空")
    private int size;

}
