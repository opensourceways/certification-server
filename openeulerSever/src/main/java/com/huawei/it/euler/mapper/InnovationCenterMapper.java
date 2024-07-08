/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.InnovationCenter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * InnovationCenterMapper
 *
 * @since 2024/07/03
 */
@Repository
public interface InnovationCenterMapper {
    InnovationCenter findById(Integer id);

    InnovationCenter findDefault();

    InnovationCenter findByName(String name);

    List<Integer> findIcIdByNameLike(String name);

    List<Integer> findIcIdInNameList(@Param("list") List<String> names);
}
