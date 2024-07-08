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
 * CompanySearchVo
 *
 * @since 2024/07/05
 */
@Data
public class CompanySearchVo {
    @Length(max = 64, message = "公司名称不能超过{max}个字符")
    private String companyName;

    private List<@Length(max = 10, message = "状态名称不能超过{max}个字符") String> status;

    @NotNull(message = "页码不能为空")
    @PositiveOrZero(message = "页码错误")
    private Integer curPage;

    @NotNull(message = "每页展示条数不能为空")
    @Range(min = 0, max = 100, message = "每页展示条数超出范围")
    private Integer pageSize;
}
