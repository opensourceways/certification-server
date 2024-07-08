/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * RegionController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
public class RegionController {
    @Autowired
    private RegionService regionService;

    /**
     * 查询所有国家
     *
     * @return JsonResponse
     */
    @GetMapping("/countries")
    public JsonResponse<List<String>> findAllCountry() {
        List<String> allCountryName = regionService.findAllCountryName();
        return JsonResponse.success(allCountryName);
    }

    /**
     * 查询所有省份
     *
     * @return JsonResponse
     */
    @GetMapping("/user/getAllProvince")
    public JsonResponse<List<String>> getAllProvince() {
        List<String> allProvince = regionService.findAllProvince();
        return JsonResponse.success(allProvince);
    }

    /**
     * 查询市区
     *
     * @param province province
     * @return JsonResponse
     */
    @GetMapping("/user/getCityByProvince")
    public JsonResponse<List<String>> getCityByProvince(@RequestParam("province") @NotBlank(message = "省份不能为空")
        @Length(max = 32, message = "省份最大不超过{max}个字符") String province) {
        List<String> cityByProvince = regionService.findCityByProvince(province);
        return JsonResponse.success(cityByProvince);
    }
}
