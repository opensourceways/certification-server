/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-18
 */
@Tag(name = "批量审批命令", description = "用于存储批量命令字段数据")
@Data
public class HardwareApprovalBatchCommand {

    @Schema(description = "业务id集合")
    @NotNull(message = "业务id集合不能为空！")
    private List<Integer> hardwareIdList;

    @Schema(description = "操作结果：1-通过，2-驳回，3-关闭，4-删除")
    @NotNull(message = "操作结果不能为空！")
    private String handlerResult;

    @Schema(description = "操作意见")
    @NotNull(message = "操作意见不能为空！")
    private String handlerComment;

    @Schema(description = "操作节点：1-数据提交，2-数据审核")
    @NotNull(message = "操作节点不能为空！")
    private String handlerNode;

    @Schema(description = "操作人uuid")
    private Integer handlerUuid;
}
