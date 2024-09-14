/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.vo.ApprovalDataVo;
import com.huawei.it.euler.model.vo.CompatibleDataSearchVo;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import com.huawei.it.euler.model.vo.FileDataVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * CompatibleDataService
 *
 * @since 2024/07/03
 */
public interface CompatibleDataService {
    /**
     * 兼容性数据材料模板下载
     *
     * @param response response
     */
    void downloadDataTemplate(HttpServletResponse response) throws IOException;

    /**
     * 上传兼容性数据表格
     *
     * @param file 文件
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<ExcelInfoVo> uploadTemplate(MultipartFile file, String uuid) throws InputException;

    /**
     * 读取兼容性数据表格
     *
     * @param loginUser 登录用户信息
     * @param fileId 文件id
     * @return JsonResponse
     */
    JsonResponse<FileDataVo> readCompatibleData(UserInfo loginUser, String fileId) throws InputException, IllegalAccessException;

    /**
     * 分页筛选返回数据列表
     *
     * @param searchVo 筛选条件
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<Map<String, Object>> findDataList(CompatibleDataSearchVo searchVo, String uuid);

    /**
     * 华为侧审核
     *
     * @param vo 审核结果
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<String> approvalCompatibleData(ApprovalDataVo vo, String uuid);
}
