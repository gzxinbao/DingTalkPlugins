package com.ipayroll.dingtalk.util;

import com.google.common.collect.Maps;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * <b>ClassUtils</b> is
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/9/6
 */
public class ClassUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = Maps.newConcurrentMap();

    static {
        PRIMITIVE_MAP.put(String.class, String.class);
        PRIMITIVE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_MAP.put(Character.class, char.class);
        PRIMITIVE_MAP.put(Double.class, double.class);
        PRIMITIVE_MAP.put(Float.class, float.class);
        PRIMITIVE_MAP.put(Integer.class, int.class);
        PRIMITIVE_MAP.put(Long.class, long.class);
        PRIMITIVE_MAP.put(Short.class, short.class);
        PRIMITIVE_MAP.put(Date.class, Date.class);
    }

    /**
     * @description 判断基本类型
     * @param clazz
     * @return boolean
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (PRIMITIVE_MAP.containsKey(clazz)) {
            return true;
        }
        return clazz.isPrimitive();
    }

    /**
     * @description 获取方法返回值类型
     * @param tartget
     * @param fieldName
     * @return
     * @return Class<?>
     */
    public static Class<?> getElementType(Class<?> tartget, String fieldName) {
        Class<?> elementTypeClass = null;
        try {
            Type type = tartget.getDeclaredField(fieldName).getGenericType();
            ParameterizedType t = (ParameterizedType) type;
            String classStr = t.getActualTypeArguments()[0].toString().replace("class ", "");
            elementTypeClass = Thread.currentThread().getContextClassLoader().loadClass(classStr);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("get fieldName[" + fieldName + "] error", e);
        }
        return elementTypeClass;
    }
}
