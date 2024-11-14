/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.cqe;

import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zhaoyan
 * @since 2024-11-14
 */
@Tag(name = "操作系统命令对象", description = "用于存储命令字段数据")
@Data
public class OsCommand {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = Update.class)
    private Integer id;

    @Schema(description = "系统名称")
    @NotNull(message = "系统名称osName不能为空", groups = Add.class)
    private String osName;

    @Schema(description = "系统版本")
    @NotNull(message = "系统版本osVersion不能为空", groups = Add.class)
    private String osVersion;

    @Schema(description = "对应欧拉版本")
    @NotNull(message = "对应欧拉版本relatedOsVersion不能为空", groups = Add.class)
    private String relatedOsVersion;

    public interface Add {}

    public interface Update {}
}
