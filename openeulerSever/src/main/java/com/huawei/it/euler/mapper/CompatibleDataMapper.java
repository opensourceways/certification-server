/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.entity.CompatibleDataApproval;
import com.huawei.it.euler.model.entity.CompatibleDataInfo;
import com.huawei.it.euler.model.vo.CompatibleDataSearchVo;
import com.huawei.it.euler.model.vo.CompatibleSimilarVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * CompatibleDataMapper
 *
 * @since 2024/07/04
 */
@Repository
public interface CompatibleDataMapper {
    void insertDataList(List<CompatibleDataInfo> dataInfoList);

    Integer selectMaxDataId();

    void insertDataApproval(List<CompatibleDataInfo> dataInfoList);

    List<CompatibleDataInfo> getDataList(CompatibleDataSearchVo searchVo);

    CompatibleDataInfo selectDataByDataId(@Param("dataId") Integer dataId);

    CompatibleDataApproval selectDataApproval(@Param("dataId") Integer dataId);

    List<String> getStatus(List<Integer> dataIdList);

    List<CompatibleDataInfo> selectDataInfoDetail(List<Integer> dataIdList);

    void updateDataInfo(@Param("list") List<Integer> dataIdList,
                        @Param("status") String updateStatus,
                        @Param("updateTime") Date updateTime);

    void insertApprovalInfo(List<CompatibleDataApproval> dataApprovalList);

    List<Integer> selectUserRoleByUuid(@Param("uuid") String uuid);

    List<CompatibleSimilarVo> selectDataByStatus();
}
