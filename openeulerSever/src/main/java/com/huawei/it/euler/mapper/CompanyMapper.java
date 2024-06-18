/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.model.entity.Company;
import com.huawei.it.euler.model.vo.UserCompanyVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CompanyMapper
 *
 * @since 2024/07/03
 */
@Repository
public interface CompanyMapper extends BaseMapper {
    void insertCompany(Company company);

    void updateCompany(Company company);

    IPage<UserCompanyVo> findCompaniesByCompanyNameAndStatus(@Param("companyName") String companyName,
                                                             @Param("list") List<Integer> status, IPage<UserCompanyVo> page);

    Company findCompanyByUserUuid(String uuid);

    Company findRegisterSuccessCompanyByUserUuid(String uuid);

    String findCompanyNameByUserUuid(String uuid);

    Company findCompanyByCreditCode(@Param("code") String code);

}
