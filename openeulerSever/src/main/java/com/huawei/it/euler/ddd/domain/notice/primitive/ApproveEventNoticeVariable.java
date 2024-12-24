/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import com.alibaba.fastjson.JSON;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsUtil;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 测评业务审核事件 通知变量对象
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Setter
public class ApproveEventNoticeVariable {

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 操作结果
     */
    private String result;
    /**
     * 当前节点
     */
    private String status;

    /**
     * 获取邮箱内容
     * @return 邮件内容字符串
     */
    public String getEmailContent(){
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("userName", userName);
        replaceMap.put("productName", productName);
        replaceMap.put("result", result);
        replaceMap.put("status", status);
        return JSON.toJSONString(replaceMap);
    }

    /**
     * 获取手机短信模板填充变量
     * @return 变量字符串
     */
    public String getPhoneTemplateParameters() {
        String[] array = new String[]{userName, productName, result, status};
        return SmsUtil.assembleContent(array);
    }
}