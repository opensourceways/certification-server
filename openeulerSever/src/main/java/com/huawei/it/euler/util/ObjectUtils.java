/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * ObjectUtils
 *
 * @since 2024/07/02
 */
@Slf4j
@Component
public class ObjectUtils {
    /**
     * 检验对象的所有属性是否为null
     *
     * @param object object
     * @return boolean
     */
    public static boolean checkFieldAllNull(Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!isEmpty(field.get(object))) {
                return false;
            }
        }
        // 父类public属性
        for (Field field : object.getClass().getFields()) {
            ReflectionUtils.makeAccessible(field);
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!isEmpty(field.get(object))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String && (object.toString().trim().isEmpty())) {
            return true;
        }
        if (object instanceof Collection && ((Collection<?>) object).isEmpty()) {
            return true;
        }
        if (object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
            return true;
        }
        return object instanceof Object[] && ((Object[]) object).length == 0;
    }
}
