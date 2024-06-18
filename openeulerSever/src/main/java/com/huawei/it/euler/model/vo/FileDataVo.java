/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * FileDataVo
 *
 * @since 2024/07/03
 */
@Data
@Accessors(chain = true)
public class FileDataVo {
    /**
     * 上传成功的数据条数
     */
    private Integer successRows;

    /**
     * 上传失败的数据条数
     */
    private Integer failedRows;

    /**
     * 上传失败的数据具体行数
     */
    private List<Integer> failedData;

    /**
     * 上传重复的数据具体行数
     */
    private List<Integer> repeatData;
}
