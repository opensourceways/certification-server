/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 英特尔通知业务消息变量
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Setter
public class ApplyIntelTestEventNoticeVariable {
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品描述
     */
    private String productFunctionDesc;
    /**
     * 使用场景
     */
    private String usageScenesDesc;
    /**
     * 产品版本
     */
    private String productVersion;
    /**
     * os名称
     */
    private String osName;
    /**
     * os版本
     */
    private String osVersion;
    /**
     * 算力平台
     */
    private String jsonHashRatePlatform;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 申请人姓名
     */
    private String userName;
    /**
     * 申请人邮箱
     */
    private String userEmail;
    /**
     * 申请人手机
     */
    private String userPhone;


    /**
     * 用户手机为空值时设置为"--"
     * @param userPhone 用户手机
     */
    public void setUserPhone(String userPhone) {
        this.userPhone = StringUtils.isEmpty(userPhone) ? "--" : userPhone;
    }

    /**
     * 算力平台消息格式化
     * @param jsonHashRatePlatform 格式化消息
     */
    public void setJsonHashRatePlatform(String jsonHashRatePlatform) {
        JSONArray jsonArray = JSON.parseArray(jsonHashRatePlatform);
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
        this.jsonHashRatePlatform = StringUtils.join(platformString, "<br>");
    }

    /**
     * 获取邮箱内容
     * @return 邮件内容字符串
     */
    public String getEmailContent(){
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("companyName", companyName);
        replaceMap.put("productName", productName);
        replaceMap.put("productFunctionDesc", productFunctionDesc);
        replaceMap.put("usageScenesDesc", usageScenesDesc);
        replaceMap.put("productVersion", productVersion);
        replaceMap.put("osName", osName);
        replaceMap.put("osVersion", osVersion);
        replaceMap.put("jsonHashRatePlatform", jsonHashRatePlatform);
        replaceMap.put("productType", productType);
        replaceMap.put("userName", userName);
        replaceMap.put("userEmail", userEmail);
        replaceMap.put("userPhone", userPhone);
        return JSON.toJSONString(replaceMap);
    }
}