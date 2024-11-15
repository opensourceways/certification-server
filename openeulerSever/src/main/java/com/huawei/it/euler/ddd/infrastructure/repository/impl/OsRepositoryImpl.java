/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.domain.masterdata.OsRepository;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.OsMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.OsPO;
import com.huawei.it.euler.ddd.infrastructure.repository.builder.OsBuilder;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作系统 持久化接口实现类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Repository
public class OsRepositoryImpl implements OsRepository {

    @Autowired
    private OsMapper mapper;

    @Autowired
    private OsBuilder builder;

    private static final List<String> SORT_COLUMN =
            new ArrayList<>(Arrays.asList("osName", "osVersion", "relatedOsVersion"));

    @Override
    public Os add(Os os) {
        OsPO osPO = builder.fromOs(os);
        mapper.insert(osPO);
        return builder.toOs(osPO);
    }

    @Override
    public void delete(Integer id) {
        OsPO osPO = mapper.selectById(id);
        if (osPO == null) {
            throw new BusinessException("操作系统不存在！");
        }
        mapper.deleteById(id);
    }

    private QueryWrapper<OsPO> createQueryWrapper(OsQuery query) {
        QueryWrapper<OsPO> queryWrapper = new QueryWrapper<>();
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        if (!StringUtils.isEmpty(query.getOsName())) {
            queryWrapper.like("os_name", query.getOsName());
        }
        if (!StringUtils.isEmpty(query.getOsVersion())) {
            queryWrapper.like("os_version", query.getOsVersion());
        }
        if (!StringUtils.isEmpty(query.getRelatedOsVersion())) {
            queryWrapper.like("related_os_version", query.getRelatedOsVersion());
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
    public List<Os> findList(OsQuery query) {
        QueryWrapper<OsPO> queryWrapper = createQueryWrapper(query);
        List<OsPO> osPOS = mapper.selectList(queryWrapper);
        return builder.toOsList(osPOS);
    }
}
