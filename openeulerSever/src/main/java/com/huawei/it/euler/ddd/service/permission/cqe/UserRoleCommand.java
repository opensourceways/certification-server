/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.permission.cqe;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zhaoyan
 * @since 2024-11-12
 */
@Tag(name = "用户角色命令对象", description = "用于存储命令字段数据")
@Data
public class UserRoleCommand {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = Update.class)
    private Integer id;

    @Schema(description = "角色id")
    @NotNull(message = "角色roleId不能为空", groups = Add.class)
    private Integer roleId;

    @Schema(description = "数据范围")
    @NotNull(message = "数据范围dataScope不能为空", groups = Add.class)
    private Integer dataScope;

    @Schema(description = "用户uuid")
    @NotNull(message = "用户uuid不能为空", groups = Add.class)
    private String uuid;

    public interface Add {}

    public interface Update {}
}
