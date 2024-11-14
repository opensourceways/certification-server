/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.permission.cqe;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-12
 */
@Tag(name = "用户角色查询对象", description = "用于存储查询命令字段数据")
@Data
public class UserRoleQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户角色id")
    private Integer id;

    @Schema(description = "角色id")
    private Integer roleId;

    @Schema(description = "角色id集合")
    private List<Integer> roleIdList;

    @Schema(description = "数据范围")
    private Integer dataScope;

    @Schema(description = "用户uuid")
    private String uuid;

    @Schema(description = "用户uuid集合")
    private List<String> uuidList;

    @Schema(description = "请求页码")
    @NotNull(message = "请求页码不能为空")
    private int current;

    @Schema(description = "请求页数量")
    @NotNull(message = "请求页数量不能为空")
    private int size;

}
