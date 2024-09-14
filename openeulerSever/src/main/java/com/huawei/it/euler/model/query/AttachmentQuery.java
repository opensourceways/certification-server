/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.query;

import lombok.Data;

/**
 * AttachmentQuery
 *
 * @since 2024/09/03
 */
@Data
public class AttachmentQuery {
    /**
     * 流程id
     */
    private Integer softwareId;

    /**
     * 文件类型
     */
    private String fileType;
}
