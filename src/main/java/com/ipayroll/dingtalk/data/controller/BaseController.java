package com.ipayroll.dingtalk.data.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ipayroll.dingtalk.data.entity.ResponseCode;
import com.ipayroll.dingtalk.data.entity.ResponseEntity;
import com.ipayroll.dingtalk.data.helper.AppHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;

/**
 * <p>
 * <b>BaseController</b> is
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/8/30
 */
public abstract class BaseController {

    private static final Map<Class<?>, String> MAPPINGS = Maps.newConcurrentMap();

    private String defaultPath = "";

    public BaseController() {
    }

    public BaseController(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    @Resource
    private AppHelper appHelper;

    /**
     * 获取模板路径
     * @param path
     * @return
     */
    protected String render(String path) {
        Class<?> clazz = getClass();
        String mapping = MAPPINGS.get(clazz);
        if (mapping == null) {
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
            if (null != requestMapping && requestMapping.value().length > 0) {
                mapping = requestMapping.value()[0];
            }
            if (StringUtils.isEmpty(mapping)) {
                mapping = StringUtils.EMPTY;
            } else {
                if (!mapping.startsWith("/")) {
                    mapping = "/" + mapping;
                }
            }
            MAPPINGS.put(clazz, mapping);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return defaultPath + mapping + path;
    }

    /**
     * 重定向
     * @param url
     * @return
     */
    protected String redirect(String url) {
        if (StringUtils.isEmpty(url)) {
            url = StringUtils.EMPTY;
        }
        return "redirect:" + url;
    }

    /**
     * 转发
     * @param url
     * @return
     */
    protected String forward(String url) {
        if (StringUtils.isEmpty(url)) {
            url = StringUtils.EMPTY;
        }
        return "forward:" + url;
    }

    /**
     * JSON响应
     * @param pojo
     * @return
     */
    protected <T> String responseCallback(T pojo) {
        ResponseEntity entity = new ResponseEntity();
        entity.setError(pojo);
        return toJSONString(entity);
    }

    protected <T> String responseCallback(T pojo, String msg) {
        ResponseEntity entity = new ResponseEntity();
        entity.setError(pojo);
        entity.setMsg(msg);
        return toJSONString(entity);
    }

    protected String responseCallback() {
        ResponseEntity entity = new ResponseEntity();
        entity.setError("success");
        return toJSONString(entity);
    }

    protected String responseCallback(String msg) {
        ResponseEntity entity = new ResponseEntity();
        entity.setMsg(msg);
        return toJSONString(entity);
    }

    protected String responseErrorCallback(String msg) {
        ResponseEntity entity = new ResponseEntity();
        entity.setCode(ResponseCode.ERROR);
        entity.setMsg(msg);
        return toJSONString(entity);
    }

    protected String responseCallback(int code, String msg) {
        ResponseEntity entity = new ResponseEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        return toJSONString(entity);
    }

    protected String toJSONString(Object obj) {
        return appHelper.toJSONString(obj);
    }

    /**
     * JSON响应
     * @param pojo
     * @param filterFields
     * @param <T>
     * @return
     */
    protected <T> String responseCallback(T pojo,List<String> filterFields) {
        ResponseEntity entity = new ResponseEntity();
        entity.setError(pojo);
        return toJsonString(entity,filterFields);
    }

    /**
     *
     * @param obj
     * @param filterFields 不序列化的字段
     * @return
     */
    protected String toJsonString(Object obj, List<String> filterFields){
        PropertyFilter proFileter = new PropertyFilter() {
            @Override
            public boolean apply(Object object, String name, Object value) {

                if(null != filterFields && filterFields.contains(name)){
                    return false;
                }
                return true;
            }
        };
        return JSON.toJSONString(obj,proFileter,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
    }

}
