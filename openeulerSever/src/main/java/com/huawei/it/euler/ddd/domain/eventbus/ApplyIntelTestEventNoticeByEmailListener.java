/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.config.extension.EmailConfig;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注释
 *
 * @author zhaoyan
 * @since 2024-12-13
 */
@Component
public class ApplyIntelTestEventNoticeByEmailListener {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailConfig emailConfig;

    @TransactionalEventListener
    @Async
    public void listener(ApplyIntelTestEvent event){
        Software software = event.getSoftware();
        List<UserInfo> userInfoList = accountService.getUserInfoList(RoleEnum.OPENATOM_INTEL.getRoleId());
        List<String> receiverList = new ArrayList<>();
//        for (UserInfo userInfo : userInfoList) {
//            if (!StringUtils.isEmpty(userInfo.getEmail())) {
//                receiverList.add(userInfo.getEmail());
//            }
//        }
        receiverList.add("zhaoyan150@huawei.com");

        String subject = "英特尔先进技术评测业务申请";

        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("companyName", software.getCompanyName());
        replaceMap.put("productName", software.getProductName());
        replaceMap.put("productFunctionDesc", software.getProductFunctionDesc());
        replaceMap.put("usageScenesDesc", software.getUsageScenesDesc());
        replaceMap.put("productVersion", software.getProductVersion());
        replaceMap.put("osName", software.getOsName());
        replaceMap.put("osVersion", software.getOsVersion());

        JSONArray jsonArray = JSON.parseArray(software.getJsonHashRatePlatform());
        List<ComputingPlatformVo> computingPlatformVos = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ComputingPlatformVo computingPlatformVo = JSON.toJavaObject(jsonObject, ComputingPlatformVo.class);
            computingPlatformVos.add(computingPlatformVo);
        }
        List<String> platformString = computingPlatformVos.stream().map(item -> {
            String platform = String.join("、", item.getServerTypes());
            return item.getPlatformName() + "/" + item.getServerProvider() + "/" + platform;
        }).toList();

        replaceMap.put("jsonHashRatePlatform", StringUtils.join(platformString, "<br>"));
        replaceMap.put("productType", software.getProductType());

        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        replaceMap.put("userName", userInfo.getUserName());
        replaceMap.put("userEmail", userInfo.getEmail());
        replaceMap.put("userPhone", userInfo.getPhone());
        String content = emailConfig.getIntelNoticeEmailContent(replaceMap);
        emailConfig.sendMail(receiverList, subject, content, new ArrayList<>());
    }
}
