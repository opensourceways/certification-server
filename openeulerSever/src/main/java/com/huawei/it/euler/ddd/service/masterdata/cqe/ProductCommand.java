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
 * @since 2024-11-14
 */
@Tag(name = "产品类型命令对象", description = "用于存储新增命令字段数据")
@Data
public class ProductCommand {
    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空", groups = Update.class)
    private Integer id;

    @Schema(description = "产品类型")
    @NotNull(message = "产品类型不能为空", groups = Add.class)
    private String productType;

    @Schema(description = "产品类型子类型")
    @NotNull(message = "产品类型子类型不能为空", groups = Add.class)
    private String productChildrenType;

    public interface Add {}

    public interface Update {}
}
