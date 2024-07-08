/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * CompatibleDataApproval
 *
 * @since 2024/07/04
 */
@Data
@Accessors(chain = true)
public class CompatibleDataApproval {
    private Integer id;

    private Integer dataId;

    private String productName;

    private Integer handlerResult;

    private String handlerComment;

    private String handler;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date handlerTime;

    private String status;
}
