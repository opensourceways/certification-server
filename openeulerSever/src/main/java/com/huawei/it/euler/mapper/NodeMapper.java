/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.Node;
import org.springframework.stereotype.Repository;

/**
 * NodeMapper
 *
 * @since 2024/07/04
 */
@Repository
public interface NodeMapper {
    void insertNode(Node node);

    Node findLatestNodeById(Integer softwareId);

    void updateNodeById(Node node);
}
