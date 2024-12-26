/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.account.UserInfoService;
import com.huawei.it.euler.ddd.domain.software.SoftwareStatistics;
import com.huawei.it.euler.ddd.service.software.cqe.SoftwareStatisticsQuery;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.util.ExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 测评业务统计查询 Application service
 *
 * @author zhaoyan
 * @since 2024-12-25
 */
@Service
public class SoftwareApplicationService {


    @Autowired
    private SoftwareMapper mapper;

    @Autowired
    private ExcelUtils excelUtils;

    public void exportStatistics(SoftwareStatisticsQuery query, HttpServletResponse response, UserInfo loginUser) throws IOException {
        List<SoftwareStatistics> statistics = mapper.statistics(query);
        if (query.getProductTypeList() != null && !query.getProductTypeList().isEmpty()){
            statistics = statistics.stream().filter(item -> query.getProductTypeList().contains(item.getProductType())).toList();
        }
        excelUtils.exportSoftWareStatistics(statistics, response);
    }

}