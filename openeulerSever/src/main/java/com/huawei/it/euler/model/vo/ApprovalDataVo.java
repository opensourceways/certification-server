/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

import java.util.List;

/**
 * ApprovalDataVo
 *
 * @since 2024/07/03
 */
@Data
public class ApprovalDataVo {
    /**
     * 数据id
     */
    private List<Integer> dataIdList;

    /**
     * 审核结果，1-通过，2-驳回
     */
    private Integer handlerResult;

    /**
     * 审核意见
     */
    private String handlerComment;
}
