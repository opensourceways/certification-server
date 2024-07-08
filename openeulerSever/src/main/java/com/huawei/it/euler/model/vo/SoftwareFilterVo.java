/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * SoftwareFilterVo
 *
 * @since 2024/07/01
 */
@Data
public class SoftwareFilterVo {
    /**
     * 页数
     */
    @NotNull(message = "页码不能为空")
    @PositiveOrZero(message = "页码错误")
    Integer pageNo;

    /**
     * 每页大小
     */
    @NotNull(message = "每页展示条数不能为空")
    @Range(min = 0, max = 100, message = "每页展示条数超出范围")
    Integer pageSize;

    /**
     * 测试机构
     */
    @Length(max = 50, message = "测试机构最大不超过{max}个字符")
    String testOrganization;

    /**
     * 操作系统名称
     */
    @Length(max = 50, message = "操作系统名称最大不超过{max}个字符")
    String osName;

    /**
     * 公司名称或产品名称
     */
    @Length(max = 255, message = "关键词最大不超过{max}个字符")
    String keyword;

    /**
     * 数据来源（upload上传导入，assessment技术测评）
     */
    List<@Length(max = 50, message = "数据来源最大不超过{max}个字符") String> dataSource;

    /**
     * 产品类型（软件、硬件，软件包括基础软件+行业软件）
     */
    List<@Length(max = 50, message = "产品类型最大不超过{max}个字符") String> productType;
}
