/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoard;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoardRepository;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.NoticeBoardMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.NoticeBoardPO;
import com.huawei.it.euler.ddd.infrastructure.repository.converter.NoticeBoardConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统公告 持久化接口实现类
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Repository
public class NoticeBoardRepositoryImpl implements NoticeBoardRepository {

    @Autowired
    private NoticeBoardMapper mapper;

    @Autowired
    private NoticeBoardConverter converter;

    @Override
    public List<NoticeBoard> findActiveList() {
        QueryWrapper<NoticeBoardPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active","y");
        queryWrapper.orderByDesc("update_time");
        List<NoticeBoardPO> noticeBoardPOList = mapper.selectList(queryWrapper);
        return noticeBoardPOList.stream().map(converter::toDO).toList();
    }

    @Override
    public NoticeBoard publish(NoticeBoard noticeBoard) {
        NoticeBoardPO noticeBoardPO = converter.toPO(noticeBoard);
        mapper.insert(noticeBoardPO);
        return converter.toDO(noticeBoardPO);
    }
}