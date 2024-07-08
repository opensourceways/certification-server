/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * ListPageUtils
 *
 * @since 2024/07/02
 */
@Component
public class ListPageUtils {
    /**
     * 分页
     *
     * @param list list
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return List<T>
     */
    public static <T> List<T> getListPage(List<T> list, int pageNum, int pageSize) {
        if (pageSize == 0 || pageNum <= 0) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 开始下标
        int startIndex = (pageNum - 1) * pageSize;
        // 结束下标
        int endIndex = pageNum * pageSize;
        // list总条数
        int total = list.size();
        // 总页数
        int pageCount = 0;
        int num = total % pageSize;
        if (num == 0) {
            pageCount = total / pageSize;
        } else {
            pageCount = total / pageSize + 1;
        }
        if (pageNum == pageCount) {
            endIndex = total;
        }
        return list.subList(startIndex, endIndex);
    }
}
