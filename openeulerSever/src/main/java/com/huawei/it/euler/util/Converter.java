/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import com.huawei.it.euler.model.vo.PageResult;

public interface Converter<S,T>{

    T convert(S source);

    default void convert(S source, T target) {}

    default  List<T>  convert(List<S> source) {
        return CollectionUtils.isEmpty(source) ? Collections.emptyList() : source.stream().map(this::convert).toList();
    }

    default PageResult<T> convert(PageResult<S> source) {
        if (ObjectUtils.isEmpty(source)) {
            return PageResult.empty();
        }else {
            List<T> target = source.getList().stream().map(this::convert).toList();
            return new PageResult<>(target, source.getTotal(), source.getPageNum(), source.getPageSize());
        }
    }
}
