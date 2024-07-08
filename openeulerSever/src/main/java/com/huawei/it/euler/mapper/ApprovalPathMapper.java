/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.model.entity.ApprovalPath;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ApprovalPathMapper
 *
 * @since 2024/07/03
 */
@Repository
public interface ApprovalPathMapper {
    IPage<ApprovalPath> findApprovalPathByPage(IPage page);

    IPage<ApprovalPath> findApprovalPathByIcAndPage(@Param("list") List<Integer> icIds, IPage page);
}
