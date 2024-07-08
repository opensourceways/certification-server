/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.AuditLog;
import com.huawei.it.euler.model.entity.ComputingPlatform;
import com.huawei.it.euler.model.entity.Os;
import com.huawei.it.euler.model.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MasterDataMapper
 *
 * @since 2024/07/02
 */
@Repository
public interface MasterDataMapper {
    List<Os> selectAllOs();

    List<Product> selectAllProduct();

    List<ComputingPlatform> selectAllComputingPlatform();

    List<String> selectAllInnovationCenter();

    Integer findOsByNameAndVersion(
            @Param("name") String name, @Param("version") String version);

    Integer findPlatformByParam(
            @Param("name") String name, @Param("provider") String provider, @Param("type") String type);

    Integer findProduct(
            @Param("productType") String productType, @Param("productChildrenType") String productChildrenType);

    void insertAuditLog(AuditLog auditLog);
}
