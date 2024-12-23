/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import com.huawei.it.euler.ddd.infrastructure.sms.SmsUtil;
import lombok.Setter;

/**
 * 企业审核通知变量
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Setter
public class CompanyApproveNoticeVariable {

    /**
     * 审批结果
     */
    private String result;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 获取手机短信模板填充变量
     * @return 变量字符串
     */
    public String getPhoneTemplateParameters() {
        String[] array = new String[]{result, comment};
        return SmsUtil.assembleContent(array);
    }
}