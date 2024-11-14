/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.cqe;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zhaoyan
 * @since 2024-11-13
 */
@Tag(name = "算力平台命令对象", description = "用于存储新增命令字段数据")
@Data
public class ComputingPlatformCommand {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = Update.class)
    private Integer id;

    @Schema(description = "算力平台")
    @NotNull(message = "算力平台不能为空", groups = Add.class)
    private String platformName;

    @Schema(description = "服务厂家")
    @NotNull(message = "服务厂家不能为空", groups = Add.class)
    private String serverProvider;

    @Schema(description = "服务器类型")
    @NotNull(message = "服务器类型不能为空", groups = Add.class)
    private String serverType;

    public interface Add {}

    public interface Update {
    }
}
