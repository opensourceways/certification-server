/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OsVo
 *
 * @since 2024/07/03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OsVo {
    private String osName;

    private List<String> osVersion;
}
