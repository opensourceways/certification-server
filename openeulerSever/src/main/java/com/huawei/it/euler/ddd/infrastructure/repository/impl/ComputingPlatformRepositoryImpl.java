/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatformRepository;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.ComputingPlatformMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.ComputingPlatformPO;
import com.huawei.it.euler.ddd.infrastructure.repository.builder.ComputingPlatformBuilder;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 算力平台 持久化接口实现类
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
@Repository
public class ComputingPlatformRepositoryImpl implements ComputingPlatformRepository {

    @Autowired
    private ComputingPlatformMapper mapper;

    @Autowired
    private ComputingPlatformBuilder builder;

    private static final List<String> SORT_COLUMN =
            new ArrayList<>(Arrays.asList("platformName", "serverProvider", "serverType"));

    @Override
    public ComputingPlatform add(ComputingPlatform computingPlatform) {
        ComputingPlatformPO computingPlatformPO = builder.fromComputingPlatform(computingPlatform);
        mapper.insert(computingPlatformPO);
        return builder.toComputingPlatform(computingPlatformPO);
    }

    @Override
    public void delete(Integer id) {
        ComputingPlatformPO computingPlatformPO = mapper.selectById(id);
        if (computingPlatformPO == null) {
            throw new BusinessException("算例平台不存在!");
        }
        mapper.deleteById(id);
    }

    private QueryWrapper<ComputingPlatformPO> createQueryWrapper(ComputingPlatformQuery query) {
        QueryWrapper<ComputingPlatformPO> queryWrapper = new QueryWrapper<>();
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        if (!StringUtils.isEmpty(query.getPlatformName())) {
            queryWrapper.like("platform_name", query.getPlatformName());
        }
        if (!StringUtils.isEmpty(query.getServerProvider())) {
            queryWrapper.like("server_provider", query.getServerProvider());
        }
        if (!StringUtils.isEmpty(query.getServerType())) {
            queryWrapper.like("server_type", query.getServerType());
        }
        if (query.getAscSort() != null && !query.getAscSort().isEmpty()) {
            List<String> ascList = query.getAscSort().stream().filter(SORT_COLUMN::contains)
                    .map(item -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item))
                    .toList();
            if (!ascList.isEmpty()) {
                queryWrapper.orderByAsc(ascList);
            }
        }
        if (query.getDescSort() != null && !query.getDescSort().isEmpty()) {
            List<String> descList = query.getDescSort().stream().filter(SORT_COLUMN::contains)
                    .map(item -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item))
                    .toList();
            if (!descList.isEmpty()) {
                queryWrapper.orderByAsc(descList);
            }
        }
        return queryWrapper;
    }

    @Override
    public List<ComputingPlatform> findList(ComputingPlatformQuery query) {
        QueryWrapper<ComputingPlatformPO> queryWrapper = createQueryWrapper(query);
        List<ComputingPlatformPO> computingPlatformPOS = mapper.selectList(queryWrapper);
        return builder.toComputingPlatformList(computingPlatformPOS);
    }

}
