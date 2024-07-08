/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.vo.EulerUserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper
 *
 * @since 2024/06/29
 */
@Repository
public interface UserMapper {
    EulerUser findByUuid(String uuid);

    List<EulerUser> findByUserId(@Param("idList") List<Integer> idList);

    void insertUser(EulerUser user);

    void updateUser(EulerUserVo user);

    int deleteUser(@Param("uuid") String uuid);
}
