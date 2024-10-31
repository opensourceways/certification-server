/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

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
@Data
public class HardwareApprovalNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 硬件设备id
     */
    @NotNull(message = "业务id不能为空！")
    private Integer hardwareId;

    /**
     * 硬件设备类型:wholeMachine-整机；boardCard-板卡
     */
    private String hardwareType;

    /**
     * 操作人uuid
     */
    private Integer handlerUuid;

    /**
     * 操作时间
     */
    private Date handlerTime;

    /**
     * 操作结果：HardwareValueEnum-RESULT*
     */
    private String handlerResult;

    /**
     * 操作意见
     */
    @NotNull(message = "操作意见不能为空！")
    private String handlerComment;

    /**
     * 操作节点：HardwareValueEnum-NODE*
     */
    private String handlerNode;

    /**
     * 操作节点名称：数据提交；数据审核；
     */
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
