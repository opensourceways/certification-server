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
 * SelectSoftwareVo
 *
 * @since 2024/07/03
 */
@Data
public class SelectSoftwareVo {
    /**
     * 产品名称
     */
    @Length(max = 64, message = "产品名称不能为空")
    private String productName;

    /**
     * 认证状态
     */
    private List<@Length(max = 10, message = "测评状态错误") String> status;

    /**
     * 测试机构
     */
    private List<@Length(max = 50, message = "测试机构最大不超过{max}个字符") String> testOrganization;

    /**
     * 产品类型
     */
    private List<@Length(max = 50, message = "产品类型最大不超过{max}个字符") String> productType;

    /**
     * 判断是否选择我的申请
     */
    private List<@Length(max = 10, message = "测评状态错误") String> selectMyApplication;

    /**
     * 当前页
     */
    @NotNull(message = "页码不能为空")
    @PositiveOrZero(message = "页码错误")
    private Integer pageNum;

    /**
     * 每页条数
     */
    @NotNull(message = "每页展示条数不能为空")
    @Range(min = 0, max = 100, message = "每页展示条数超出范围")
    private Integer pageSize;
}
