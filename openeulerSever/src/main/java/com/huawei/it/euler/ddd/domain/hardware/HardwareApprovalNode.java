/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

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
     * 操作结果：1-通过；2-驳回
     */
    private String handlerResult;

    /**
     * 操作意见
     */
    private String handlerComment;

    /**
     * 操作节点：wait_apply-数据提交；wait_approval-数据审核
     */
    private String handlerNode;

    /**
     * 操作节点名称：数据提交；数据审核；
     */
    private String handlerNodeName;

    public HardwareApprovalNode apply(String hardwareType) {
        this.setHardwareType(hardwareType);
        this.setHandlerTime(new Date());
        this.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setHandlerNodeName(HardwareValueEnum.NODE_WAIT_APPLY.getText());
        return this;
    }

    public HardwareApprovalNode approval(String hardwareType){
        this.setHardwareType(hardwareType);
        this.setHandlerTime(new Date());
        this.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        this.setHandlerNodeName(HardwareValueEnum.NODE_WAIT_APPROVE.getText());
        return this;
    }

}
