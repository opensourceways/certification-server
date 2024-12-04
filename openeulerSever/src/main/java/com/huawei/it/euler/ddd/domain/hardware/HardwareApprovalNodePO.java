/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 硬件审批操作记录表
 *
 * @author zhaoyan
 * @since 2024-10-09
 */
@Getter
@Setter
@TableName("hardware_approval_node_t")
public class HardwareApprovalNodePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 硬件设备id
     */
    @TableField("hardware_id")
    private Integer hardwareId;

    /**
     * 硬件设备类型:wholeMachine-整机；boardCard-板卡
     */
    @TableField("hardware_type")
    private String hardwareType;

    /**
     * 操作人uuid
     */
    @TableField("handler_uuid")
    private Integer handlerUuid;

    /**
     * 操作时间
     */
    @TableField("handler_time")
    private Date handlerTime;

    /**
     * 操作结果：1-通过；2-驳回
     */
    @TableField("handler_result")
    private String handlerResult;

    /**
     * 操作意见
     */
    @TableField("handler_comment")
    private String handlerComment;

    /**
     * 操作节点：1-待提交；2-待审批
     */
    @TableField("handler_node")
    private String handlerNode;

    /**
     * 操作节点名称：提交；审批；
     */
    @TableField("handler_node_name")
    private String handlerNodeName;
}
