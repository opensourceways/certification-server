/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.huawei.it.euler.model.entity.Node;

/**
 * NodeMapper
 *
 * @since 2024/07/04
 */
@Repository
public interface NodeMapper {
    void insertNode(Node node);

    Node findLatestNodeById(Integer softwareId);

    Node findLatestFinishedNode(@Param("softwareId") Integer softwareId, @Param("status") Integer status);

    void updateNodeById(Node node);
}
