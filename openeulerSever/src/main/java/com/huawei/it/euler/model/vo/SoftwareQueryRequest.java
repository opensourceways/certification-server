/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * SelectSoftwareVo
 *
 * @since 2024/07/03
 */
@Data
public class SoftwareQueryRequest {
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
     * 认证状态ID
     */
    private List< Integer> statusId;

    /**
     * 测试机构
     */
    private List<@Length(max = 50, message = "测评机构最大不超过{max}个字符") String> testOrganization;

    /**
     * 测试机构ID
     */
    private List<Integer> testOrgId;

    /**
     * 产品类型
     */
    private List<@Length(max = 50, message = "产品类型最大不超过{max}个字符") String> productType;

    /**
     * 判断是否选择我的申请
     */
    private List<@Length(max = 5, message = "筛选申请人错误") String> selectMyApplication;

    /**
     * 当前页
     */
//    @NotNull(message = "页码不能为空")
//    @PositiveOrZero(message = "页码错误")
    private Integer pageNum;

    /**
     * 每页条数
     */
//    @NotNull(message = "每页展示条数不能为空")
//    @Range(min = 0, max = 100, message = "每页展示条数超出范围")
    private Integer pageSize;

    /**
     * 认证完成时间筛选-开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginCertificationTime;

    /**
     * 认证完成时间筛选-结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endCertificationTime;

    /**
     * 正序排列字段集合
     */
    private List<String> ascSort;

    /**
     * 倒叙排列字段集合
     */
    private List<String> descSort;
}
