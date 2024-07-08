/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data
 *
 * @since 2024/07/04
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Data<T> {
    @JsonProperty("data")
    private T data;
}
