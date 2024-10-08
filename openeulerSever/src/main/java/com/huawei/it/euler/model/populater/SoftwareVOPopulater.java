/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.model.populater;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import com.huawei.it.euler.model.vo.SoftwareVo;

@Component
public class SoftwareVOPopulater implements Populater<SoftwareVo> {

    @Override
    public SoftwareVo populate(SoftwareVo source) {
        List<ComputingPlatformVo> platforms = JSON.parseArray(source.getHashratePlatform(), ComputingPlatformVo.class);
        source.setHashratePlatformList(platforms);
        source.setHashratePlatformNameList(joinPlatformNames(platforms));
        source.setPlatforms(formatPlatforms(platforms));
        return source;
    }

    @Override
    public List<SoftwareVo> populate(List<SoftwareVo> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        source.forEach(this::populate);
        return source;
    }

    private String joinPlatformNames(List<ComputingPlatformVo> platforms) {
        return platforms.stream().map(ComputingPlatformVo::getPlatformName)
            .filter(name -> name != null && !name.isEmpty()).collect(Collectors.joining("/"));
    }

    private List<String> formatPlatforms(List<ComputingPlatformVo> platforms) {
        return platforms.stream().map(p -> String.format("%s/%s/%s", p.getPlatformName(), p.getServerProvider(),
            String.join("、", p.getServerTypes()))).collect(Collectors.toList());
    }
}
