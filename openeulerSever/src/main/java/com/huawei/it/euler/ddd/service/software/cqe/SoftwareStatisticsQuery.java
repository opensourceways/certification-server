/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software.cqe;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 测评业务统计查询对象
 *
 * @author zhaoyan
 * @since 2024-12-25
 */
@Tag(name = "测评业务统计查询对象", description = "测评业务统计查询对象")
@Data
public class SoftwareStatisticsQuery {
    /**
     * 时间筛选对象
     */
    @Schema(description = "时间筛选对象：申请-application_time；签发-certification_time")
    @NotNull(message = "时间筛选对象不能为空！")
    private String dateKey;

    /**
     * 统计区间：开始日期
     */
    @Schema(description = "时间筛选：开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginDate;

    /**
     * 统计区间：截止日期
     */
    @Schema(description = "时间筛选对象：截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    /**
     * 测评机构
     */
    @Schema(description = "测评机构集合")
    private List<Integer> testOrgIdList;

    /**
     * 产品类型
     */
    @Schema(description = "产品类型集合")
    private List<String> productTypeList;
}
