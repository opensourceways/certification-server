/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.mapper.ApprovalPathMapper;
import com.huawei.it.euler.mapper.ApprovalPathNodeMapper;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.ApprovalPath;
import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.model.entity.InnovationCenter;
import com.huawei.it.euler.model.vo.ApprovalPathSearchVo;
import com.huawei.it.euler.model.vo.ApprovalPathVo;
import com.huawei.it.euler.service.ApprovalPathService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.StringPropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApprovalPathServiceImpl
 *
 * @since 2024/07/03
 */
@Service
public class ApprovalPathServiceImpl implements ApprovalPathService {
    @Autowired
    private ApprovalPathMapper approvalPathMapper;

    @Autowired
    private ApprovalPathNodeMapper approvalPathNodeMapper;

    @Autowired
    private InnovationCenterMapper innovationCenterMapper;

    @Autowired
    private EncryptUtils encryptUtils;

    @Override
    public IPage<ApprovalPathVo> findApprovalPathByPage(ApprovalPathSearchVo approvalPathSearchVo,
                                                        Page<ApprovalPathVo> page) {
        IPage<ApprovalPath> approvalPathIPage = getApprovalPathIPage(approvalPathSearchVo, page);
        IPage<ApprovalPathVo> approvalPathVoIPage = getApprovalPathVoPage(approvalPathIPage);
        List<ApprovalPath> approvalPaths = approvalPathIPage.getRecords();
        List<ApprovalPathVo> approvalPathVos = new ArrayList<>();
        for (ApprovalPath approvalPath : approvalPaths) {
            ApprovalPathVo approvalPathVo = new ApprovalPathVo();
            InnovationCenter ic = innovationCenterMapper.findById(approvalPath.getIcId());
            approvalPathVo.setInnovationCenterName(ic.getName());
            List<ApprovalPathNode> approvalPathNodes = approvalPathNodeMapper.findNodeByIcId(approvalPath.getIcId());
            for (ApprovalPathNode approvalPathNode : approvalPathNodes) {
                switch (approvalPathNode.getSoftwareStatus()) {
                    case 2:
                        approvalPathVo.setPlanReview(getHandlerName(approvalPathNode));
                        break;
                    case 4:
                        approvalPathVo.setReportFirstTrial(getHandlerName(approvalPathNode));
                        break;
                    case 5:
                        approvalPathVo.setReportReexamination(getHandlerName(approvalPathNode));
                        break;
                    case 6:
                        approvalPathVo.setCertFirstTrail(getHandlerName(approvalPathNode));
                        break;
                    case 8:
                        approvalPathVo.setCertTeexamination(getHandlerName(approvalPathNode));
                        break;
                    default:
                        break;
                }
            }
        }
        approvalPathVoIPage.setRecords(approvalPathVos);
        return approvalPathVoIPage;
    }

    private String getHandlerName(ApprovalPathNode approvalPathNode) {
        return approvalPathNode.getUsername() + "/" +
                StringPropertyUtils.reducePhoneSensitivity(encryptUtils.aesDecrypt(approvalPathNode.getUserTelephone()));
    }

    private IPage<ApprovalPathVo> getApprovalPathVoPage(IPage<ApprovalPath> approvalPathIPage) {
        IPage<ApprovalPathVo> approvalPathVoIPage = new Page<>();
        approvalPathVoIPage.setPages(approvalPathIPage.getPages());
        approvalPathVoIPage.setCurrent(approvalPathIPage.getCurrent());
        approvalPathVoIPage.setTotal(approvalPathIPage.getTotal());
        approvalPathVoIPage.setSize(approvalPathIPage.getSize());
        return approvalPathVoIPage;
    }

    private IPage<ApprovalPath> getApprovalPathIPage(ApprovalPathSearchVo approvalPathSearchVo, Page page) {
        IPage<ApprovalPath> approvalPathIPage;
        String searchIcName = approvalPathSearchVo.getSearchIcName();
        List<String> icNameList = approvalPathSearchVo.getIcNameList();
        if (icNameList == null) {
            icNameList = new ArrayList<>();
        }
        if (StringUtils.isEmpty(searchIcName) && icNameList.isEmpty()) {
            approvalPathIPage = approvalPathMapper.findApprovalPathByPage(page);
        } else if (!StringUtils.isEmpty(searchIcName) && icNameList.isEmpty()) {
            List<Integer> icIds = innovationCenterMapper.findIcIdByNameLike(searchIcName);
            if (icIds.isEmpty()) {
                icIds.add(-1);
            }
            approvalPathIPage = approvalPathMapper.findApprovalPathByIcAndPage(icIds, page);
        } else if (StringUtils.isEmpty(searchIcName) && !icNameList.isEmpty()) {
            List<Integer> icIds = innovationCenterMapper.findIcIdInNameList(icNameList);
            if (icIds.isEmpty()) {
                icIds.add(-1);
            }
            approvalPathIPage = approvalPathMapper.findApprovalPathByIcAndPage(icIds, page);
        } else {
            List<Integer> icIds = innovationCenterMapper.findIcIdByNameLike(searchIcName);
            icIds.addAll(innovationCenterMapper.findIcIdInNameList(icNameList));
            icIds = icIds.stream().distinct().collect(Collectors.toList());
            approvalPathIPage = approvalPathMapper.findApprovalPathByIcAndPage(icIds, page);
        }
        return approvalPathIPage;
    }
}
