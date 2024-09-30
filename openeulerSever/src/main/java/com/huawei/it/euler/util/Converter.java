/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

public interface Converter<S,T>{

    T convert(S source);

    default void convert(S source, T target) {}

    default  List<T>  convert(List<S> source) {
        return CollectionUtils.isEmpty(source) ? Collections.emptyList() : source.stream().map(this::convert).toList();
    }
}
