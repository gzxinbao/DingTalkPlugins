package com.ipayroll.dingtalk.data.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * <b>AppHelper</b> is
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/5/20
 */
@Component
public class AppHelper {

    private static final String MARK_MOBILE_REGEX = "(?<=[\\d]{3})\\d(?=[\\d]{4})";

    private static final String MARK_CARD_REGEX = "\\d(?=[\\d]{3})";

    private static final String MARK_EMAIL_REGEX = "(?<=[\\w]{3})\\w+?";

    private static final String MARK_NAME_REGEX = "[^x00-xff]|\\w";

    /**
     * 是否为异步请求
     *
     * @param request
     * @return
     */
    public boolean isAjaxType(HttpServletRequest request) {
        String jsonHeader = request.getHeader("Content-Type");
        String acceptHeader = request.getHeader("accept");
        if (jsonHeader == null) {
            jsonHeader = acceptHeader;
        }
        if ((jsonHeader != null && jsonHeader.indexOf("application/json") > -1)
                || (( request.getHeader("X-Requested-With") != null)
                && (request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1 ))) {
            return true;
        }
        if ((acceptHeader != null && acceptHeader.indexOf("application/json") > -1)
                || (( request.getHeader("X-Requested-With") != null))) {
            return true;
        }
        return false;
    }

    /**
     * 获取IP
     *
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {

        if (null == request) {
            return "unknown";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isEmpty(ip)) {
            return request.getLocalAddr();
        }
        int index = ip.indexOf(',');
        if (ip.indexOf(',') > 0) {
            ip = ip.substring(0, index);
        }
        if (ip.startsWith("0:0:0:0")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public String getLocalIp() {
        return "127.0.0.1";
    }

    /**
     * 生成订单号
     * @param userId
     * @param code
     * @return
     */
    public String generatorNo(Long userId, int code) {
        StringBuffer orderNo = new StringBuffer(18);

        // 获取业务码
        orderNo.append(code);

        // 获取时间
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyMMddHHmm");

        // 处理时间
        String firstDate = date.substring(0, 1);
        Integer _firstDate = 0;
        try {
            _firstDate = Integer.valueOf(firstDate);
        } catch (NumberFormatException e) {
            _firstDate = 0;
        }
        int salt = code + _firstDate - 1;

        orderNo.append(salt);

        date = date.substring(1);

        orderNo.append(date);

        String userid = String.valueOf(userId);
        int length = userid.length();
        if (length >= 2) {
            userid = userid.substring(length - 2, length);
        } else {
            userid = "0" + userid;
        }

        orderNo.append(userid);
        orderNo.append(dateTime.toString("SSS"));

        return orderNo.toString();
    }

    /**
     * FastJson
     * @param obj
     * @return
     */
    public String toJSONString(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
    }

    /**
     * 手机号码打*号
     * @param mobile
     * @return
     */
    public String markMobile(String mobile) {
        if (StringUtils.isNotEmpty(mobile)) {
            mobile = mobile.replaceAll(MARK_MOBILE_REGEX, "*");
        }
        return mobile;
    }

    /**
     * 身份证号、银行卡号打*号
     * @param card
     * @return
     */
    public String markCard(String card) {
        if (StringUtils.isNotEmpty(card)) {
            card = card.replaceAll(MARK_CARD_REGEX, "*");
        }
        return card;
    }

    /**
     * 姓名打*号
     * @param name
     * @return
     */
    public String markName(String name){
        if (StringUtils.isNotEmpty(name)) {
            name = name.substring(0, 1) + name.substring(1).replaceAll(MARK_NAME_REGEX, "*");
        }
        return name;
    }

    public String markUsername(String username) {
        if (StringUtils.isNotEmpty(username)) {
            int length = username.length();

            if (length <= 1) {
                username = "*";
            }
            else if (length == 2) {
                username = username.replaceAll("(?<=\\w{0})\\w(?=\\w{1})", "*");
            }
            else if (length > 2 && length <= 6) {
                username = username.replaceAll("(?<=\\w{1})\\w(?=\\w{1})", "*");
            }
            else if (length == 7) {
                username = username.replaceAll("(?<=\\w{1})\\w(?=\\w{2})", "*");
            }
            else if (length == 8) {
                username = username.replaceAll("(?<=\\w{2})\\w(?=\\w{2})", "*");
            }
            else if (length == 9) {
                username = username.replaceAll("(?<=\\w{2})\\w(?=\\w{3})", "*");
            }
            else if (length == 10) {
                username = username.replaceAll("(?<=\\w{3})\\w(?=\\w{3})", "*");
            }
            else {
                username = username.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
            }
        }
        return username;
    }

    public String markEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            String[] array = email.split("@");
            email = array[0].replaceAll(MARK_EMAIL_REGEX, "*") + "@" + array[1];
        }
        return email;
    }

    /**
     * 获取UUID
     * @return
     */
    public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 从字符串中获取参数
     * @param params
     * @param key
     * @return
     */
    public String getParam(String params, String key) {
        String value = null;
        if (StringUtils.isNotEmpty(params)) {
            List<NameValuePair> list = URLEncodedUtils.parse(params, Charset.forName("UTF-8"));
            for (NameValuePair nv : list) {
                if (key.equals(nv.getName())) {
                    value = nv.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public String null2String(String str) {
        return str == null ? "" : str;
    }
}
