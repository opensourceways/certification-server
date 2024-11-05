/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 硬件审批操作记录表
 *
 * @author zhaoyan
 * @since 2024-10-09
 */
@Tag(name = "审批节点对象",description = "用于节点审批记录")
@Data
public class HardwareApprovalNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "硬件设备id")
    @NotNull(message = "业务id不能为空！")
    private Integer hardwareId;

    @Schema(description = "硬件设备类型:wholeMachine-整机；boardCard-板卡")
    private String hardwareType;

    @Schema(description = "操作人uuid")
    private Integer handlerUuid;

    @Schema(description = "操作时间")
    private Date handlerTime;

    @Schema(description = "操作结果：HardwareValueEnum-RESULT*")
    private String handlerResult;

    @Schema(description = "操作意见")
    @NotNull(message = "操作意见不能为空！")
    private String handlerComment;

    @Schema(description = "操作节点：HardwareValueEnum-NODE*")
    private String handlerNode;

    @Schema(description = "操作节点名称：数据提交；数据审核；")
    private String handlerNodeName;

    public HardwareApprovalNode action(String hardwareType, String status, String result) {
        this.setHardwareType(hardwareType);
        this.setHandlerNode(status);
        this.setHandlerNodeName(HardwareValueEnum.getTextByValue(status));
        this.setHandlerResult(result);
        this.setHandlerTime(new Date());
        return this;
    }

    public HardwareApprovalNode apply(String hardwareType) {
        this.setHardwareType(hardwareType);
        this.setHandlerTime(new Date());
        this.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setHandlerNodeName(HardwareValueEnum.NODE_WAIT_APPLY.getText());
        return this;
    }
}
