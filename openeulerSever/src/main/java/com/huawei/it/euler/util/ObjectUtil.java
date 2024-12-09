/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * 注释
 *
 * @author zhaoyan
 * @since 2024-12-05
 */
public class ObjectUtil {

    public static boolean checkFieldAllNull(Object object){
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                if (!isEmpty(field.get(object))){
                    return false;
                }
                field.setAccessible(false);
            }
            for (Field field : object.getClass().getFields()) {
                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                if (!isEmpty(field.get(object))){
                    return false;
                }
                field.setAccessible(false);
            }
            return true;
        } catch (IllegalAccessException e) {
            return true;
        }
    }

    public static boolean isEmpty(Object object){
        if (object == null){
            return true;
        }
        if (object instanceof String && (object.toString().isEmpty())) {
            return true;
        }
        if (object instanceof Collection<?> && ((Collection<?>) object).isEmpty()){
            return true;
        }
        if (object instanceof Map && ((Map<?, ?>) object).isEmpty()){
            return true;
        }
        return object instanceof Object[] && ((Object[]) object).length == 0;
    }

}
