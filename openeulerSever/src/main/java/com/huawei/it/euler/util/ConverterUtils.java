/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.mapstruct.Named;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConverterUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("Asia/Shanghai"));

    @Named("dateToString")
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return FORMATTER.format(date.toInstant());
    }
}
