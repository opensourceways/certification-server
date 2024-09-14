/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.ApprovalPathNode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ApprovalPathNodeMapper
 *
 * @since 2024/07/03
 */
@Repository
public interface ApprovalPathNodeMapper {

    List<ApprovalPathNode> findNodeByAsId(@Param("asId") Integer asId);

    ApprovalPathNode findNodeByAsIdAndStatus(@Param("asId") Integer asId, @Param("status") Integer status);

}
