/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.ApprovalScenario;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ApprovalPathMapper
 *
 * @since 2024/07/03
 */
@Repository
public interface ApprovalScenarioMapper {

    ApprovalScenario findById(Integer icId);

    List<ApprovalScenario> findByIcId(Integer icId);
}
