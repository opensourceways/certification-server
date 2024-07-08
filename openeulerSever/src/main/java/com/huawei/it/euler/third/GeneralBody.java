/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.*;

/**
 * GeneralBody
 *
 * @since 2024/07/04
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralBody<T> {
    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("attributes")
    private T attributes;
}
