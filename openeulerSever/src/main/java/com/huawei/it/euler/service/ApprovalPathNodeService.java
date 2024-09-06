/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.huawei.it.euler.model.entity.ApprovalPathNode;

import java.util.List;

/**
 * ApprovalPathNodeService
 *
 * @since 2024/07/03
 */
public interface ApprovalPathNodeService {

    /**
     * Find approval node by approval scenario id and software status.
     * @param asId approval scenario id
     * @param status software status
     * @return approval node
     */
    ApprovalPathNode findANodeByAsIdAndSoftwareStatus(Integer asId, Integer status);



    /**
     * Find approval node by approval scenario id.
     * @param icId
     * @return
     */
    List<ApprovalPathNode> findNodeByAsId(Integer icId);

}
