/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * 公司信息Vo
 *
 * @since 2024/06/29
 */
@Data
public class CompanyVo {
    /**
     * 日期校验正则 yyyy-mm-dd
     */
    public static final String DATE_VALIDATE_REGEX = "^$|^(?:(?!0000)[0-9]{4}\\-(?:(?:0[13578]|1[02])" +
            "(?:\\-0[1-9]|\\-[12][0-9]|\\-3[01])|(?:0[469]|11)(?:\\-0[1-9]|\\-[12][0-9]|\\-30)" +
            "|02(?:\\-0[1-9]|\\-1[0-9]|\\-2[0-8]))|(?:(((\\d{2})(0[48]|[2468][048]|[13579][26])|(([02468][048])" +
            "|([13579][26]))00))\\-02\\-29))$\n";

    /**
     * 邮箱校验正则，不支持中文
     */
    public static final String EMAIL_VALIDATE_REGEX =
            "^[A-Za-z0-9_\\-.!#$%&'*+\\-/=?^_`{|}~]+@[A-Za-z0-9_\\-.]+\\.[A-Za-z]{2,8}$";

    /**
     * id
     */
    @JsonIgnore
    private Integer id;

    /**
     * 申请状态
     */
    @PositiveOrZero(message = "申请状态取值错误")
    private Integer status;

    /**
     * logo文件id
     */
    @NotBlank(message = "logo不能为空", groups = companyAuthentication.class)
    private String logo;

    /**
     * 用户电话号码
     */
    private String userTelephone;

    /**
     * logo文件名
     */
    @NotBlank(message = "logo名称不能为空", groups = companyAuthentication.class)
    private String logoName;

    /**
     * 企业注册国家/地区
     */
    @NotBlank(message = "地区字段不能为空", groups = companyAuthentication.class)
    @Length(max = 32, message = "地区字段超长", groups = companyAuthentication.class)
    private String region;

    /**
     * 企业营业执照文件id
     */
    @NotBlank(message = "营业执照不能为空", groups = companyAuthentication.class)
    private String license;

    /**
     * 企业营业执照文件名
     */
    @NotBlank(message = "营业执照不能为空", groups = companyAuthentication.class)
    private String licenseName;

    /**
     * 企业名
     */
    @NotBlank(message = "公司名称不能为空", groups = companyAuthentication.class)
    @Length(max = 64, message = "公司名称超长", groups = companyAuthentication.class)
    private String companyName;

    /**
     * 企业信用代码
     */
    @NotBlank(message = "企业信用代码不能为空", groups = companyAuthentication.class)
    @Length(max = 32, message = "企业信用代码超长", groups = companyAuthentication.class)
    private String creditCode;

    /**
     * 企业注册地址
     */
    @Length(max = 1000, message = "企业注册地址超长", groups = companyAuthentication.class)
    private String address;

    /**
     * 企业法人
     */
    @NotBlank(message = "企业法人不能为空", groups = companyAuthentication.class)
    @Length(max = 50, message = "企业法人名称超长", groups = companyAuthentication.class)
    private String legalPerson;

    /**
     * 企业注册资金
     */
    @Length(max = 32, message = "企业注册资金超长", groups = companyAuthentication.class)
    private String registrationCapital;

    /**
     * 企业注册日期
     */
    @Nullable
    @Pattern(regexp = DATE_VALIDATE_REGEX, message = "注册日期格式错误", groups = companyAuthentication.class)
    private String registrationDate;

    /**
     * 企业营业执照过期日期
     */
    @Nullable
    @Pattern(regexp = DATE_VALIDATE_REGEX, message = "企业营业执照过期日期格式错误", groups = companyAuthentication.class)
    private String expirationDate;

    /**
     * 评审意见
     */
    private String approvalComment;

    /**
     * 企业邮箱
     */
    @NotBlank(message = "企业邮箱不能为空", groups = companyAuthentication.class)
    @Length(max = 64, message = "企业邮箱超长", groups = companyAuthentication.class)
    @Pattern(regexp = EMAIL_VALIDATE_REGEX, message = "邮箱格式错误", groups = companyAuthentication.class)
    private String companyMail;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 是否通过工商注册校验
     */
    private Boolean isCheckedPass;

    /**
     * 企业实名认证申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date applyTime;

    public interface companyAuthentication {}
}
