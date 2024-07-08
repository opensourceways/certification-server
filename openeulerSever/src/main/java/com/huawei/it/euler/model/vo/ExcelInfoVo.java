/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ExcelInfoVo
 *
 * @since 2024/07/03
 */
@Data
@AllArgsConstructor
public class ExcelInfoVo {
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private String fileSize;
}
