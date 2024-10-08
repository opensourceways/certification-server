/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.model.populater;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.huawei.it.euler.model.vo.SoftwareVo;

@Component
public class SoftwareVOPopulater implements Populater<SoftwareVo> {

    @Override
    public List<SoftwareVo> populate(List<SoftwareVo> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        source.forEach(softwareVO -> {

        });
        return source;
    }

}
