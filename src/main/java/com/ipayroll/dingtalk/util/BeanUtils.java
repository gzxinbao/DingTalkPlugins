package com.ipayroll.dingtalk.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.apache.commons.beanutils.ConversionException;

import javax.sql.rowset.serial.SerialException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <b>BeanUtils</b> is
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/9/6
 */
@Slf4j
public class BeanUtils {

    private static final Map<String, BeanCopier> beanCopierMap = Maps.newConcurrentMap();

    public static <T> T mapToObject(Map<String,Object> source, Class<T> target) throws SerialException {
        T t = null;
        try {
            t = target.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(t, source);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ConversionException e){
            log.error("数据转换异常{}",e);
            throw new NumberFormatException(e.getMessage());
        }
        return t;
    }



    /**
     * Bean copy
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, Class<T> target) {
        T dest = null;
        if (source != null) {
            try {
                dest = target.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + target.getName() + "] instance error", e);
            }
            BeanCopier beanCopier = getBeanCopier(source.getClass(), target);
            beanCopier.copy(source, dest, null);
        }
        return dest;
    }

    /**
     * Bean copy
     * @param source
     * @param dest
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, T dest) {
        if (source != null) {

            BeanCopier beanCopier = getBeanCopier(source.getClass(), dest.getClass());
            beanCopier.copy(source, dest, null);
        }
        return dest;
    }

    /**
     * Bean deep copy
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyDeep(Object source, Class<T> target) {
        T dest = null;

        if (source != null) {

            try {
                dest = target.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + target.getName() + "] instance error", e);
            }
            BeanCopier beanCopier = getBeanCopierConverter(source.getClass(), target);
            beanCopier.copy(source, dest, new DeepCopyConverter(target));
        }
        return dest;
    }

    /**
     * @description 获取BeanCopier
     * @param source
     * @param target
     * @return
     * @return BeanCopier
     */
    private static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {
        String beanCopierKey = generateBeanKey(source, target);
        if (beanCopierMap.containsKey(beanCopierKey)) {
            return beanCopierMap.get(beanCopierKey);
        } else {
            BeanCopier beanCopier = BeanCopier.create(source, target, false);
            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);
        }
        return beanCopierMap.get(beanCopierKey);
    }

    /**
     * @description 获取BeanCopier
     * @param source
     * @param target
     * @return
     * @return BeanCopier
     */
    private static BeanCopier getBeanCopierConverter(Class<?> source, Class<?> target) {
        String beanCopierKey = generateBeanKey(source, target);
        if (beanCopierMap.containsKey(beanCopierKey)) {
            return beanCopierMap.get(beanCopierKey);
        } else {
            BeanCopier beanCopier = BeanCopier.create(source, target, true);
            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);
        }
        return beanCopierMap.get(beanCopierKey);
    }

    /**
     * @description 生成两个类的key
     * @param source
     * @param target
     * @return
     * @return String
     */
    private static String generateBeanKey(Class<?> source, Class<?> target) {
        return source.getName() + "@" + target.getName();
    }

    public static class DeepCopyConverter implements Converter {

        /**
         * The Target.
         */
        private Class<?> target;

        /**
         * Instantiates a new Deep copy converter.
         *
         * @param target
         *            the target
         */
        public DeepCopyConverter(Class<?> target) {
            this.target = target;
        }

        @Override
        public Object convert(Object value, Class targetClazz, Object methodName) {
            if (value instanceof List) {
                List values = (List) value;
                List retList = new ArrayList<>(values.size());
                for (final Object source : values) {
                    String tempFieldName = methodName.toString().replace("set",
                            "");
                    String fieldName = tempFieldName.substring(0, 1)
                            .toLowerCase() + tempFieldName.substring(1);
                    Class clazz = ClassUtils.getElementType(target, fieldName);
                    retList.add(BeanUtils.copy(source, clazz));
                }
                return retList;
            } else if (value instanceof Map) {
            } else if (!ClassUtils.isPrimitive(targetClazz)) {
                return BeanUtils.copy(value, targetClazz);
            }
            return value;
        }
    }



}
