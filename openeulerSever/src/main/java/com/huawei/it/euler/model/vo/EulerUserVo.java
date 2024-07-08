/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.it.euler.model.entity.EulerUser;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户信息Vo
 *
 * @since 2024/06/29
 */
@Data
public class EulerUserVo {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_-]{1,16}$", message = "用户名最大长度为16，支持汉字、大小写字母、数字、下划线_和短横线-")
    private String username;

    @JsonIgnore
    private String telephone;

    @Pattern(regexp = "^$|" + CompanyVo.EMAIL_VALIDATE_REGEX, message = "邮箱格式不正确")
    private String mail;

    @Length(max = 50, message = "省份最大不能超过{max}个字符")
    private String province;

    @Length(max = 50, message = "城市/市区不能超过{max}个字符")
    private String city;

    @NotEmpty(message = "uuid不能为空")
    private String uuid;

    private List<String> roles;

    /**
     * 获取VO
     *
     * @param eulerUser eulerUser
     */
    public void getVo(EulerUser eulerUser) {
        this.setUuid(eulerUser.getUuid());
        this.setUsername(eulerUser.getUsername());
        this.setCity(eulerUser.getCity());
        this.setProvince(eulerUser.getProvince());
        this.setMail(eulerUser.getMail());
        this.setTelephone(eulerUser.getTelephone());
    }
}
