/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * CompatibleDataSearchVo
 *
 * @since 2024/07/03
 */
@Data
public class CompatibleDataSearchVo {
    /**
     * 产品名称
     */
    @Length(max = 255, message = "产品名称不能超过{max}个字符")
    private String productName;

    /**
     * 状态
     */
    private List<@Length(max = 10, message = "状态名称不能超过{max}个字符") String> statusStr;

    /**
     * 产品类型
     */
    private List<@Length(max = 255, message = "产品类型名称不能超过{max}个字符") String> productTypes;

    /**
     * 当前页数
     */
    @NotNull(message = "页码不能为空")
    @PositiveOrZero(message = "页码错误")
    private Integer curPage;

    /**
     * 每页条数
     */
    @NotNull(message = "每页条数不能为空")
    @Range(min = 0, max = 100, message = "每页条数超出范围")
    private Integer pageSize;

    /**
     * 用户唯一标识uuid
     */
    @Length(max = 255, message = "用户uuid不能超过{max}个字符")
    private String uuid;
}
