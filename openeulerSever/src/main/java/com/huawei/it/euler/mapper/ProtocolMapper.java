/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.Protocol;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 协议mapper
 *
 * @since 2024/06/29
 */
@Repository
public interface ProtocolMapper {
    int insertUserSignProtocol(Protocol protocol);

    Protocol selectProtocolByType(@Param("protocolType") Integer protocolType, @Param("uuid") String uuid);

    int cancelSignedProtocol(@Param("id") Integer id);

    Protocol selectProtocolDesc(@Param("protocolType") Integer protocolType, @Param("uuid") String uuid);
}
