/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.model.populater;

import java.util.List;

public interface Populater <T> {
    default T populate(T source) { return source; }

    default List<T> populate(List<T> source) { return source; }
}
