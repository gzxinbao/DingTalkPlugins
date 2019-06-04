package com.ipayroll.dingtalk.exception;

import com.ipayroll.dingtalk.data.helper.MessageHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * <b>ServiceException</b> is 业务异常
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/8/30
 */
public class ServiceException extends RuntimeException {

    private static final String SERVER_ERROR = "server.error";

    private String code;

    private Integer responseCode;

    private Object[] args;

    private String target;

    public ServiceException(String code, Object... args) {
        this(code, null, args);
    }

    public ServiceException(String code, String target, Object... args) {
        super();
        this.code = code;
        this.args = args;
        this.target = target;
    }

    public ServiceException(int responseCode, String code, Object... args) {
        super();
        this.responseCode = responseCode;
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String getMessage() {
        String message = null;
        if (StringUtils.isEmpty(code)) {
            code = SERVER_ERROR;
        }
        if (StringUtils.isNotEmpty(code)) {
            message = MessageHelper.message(code, args);
        }
        if (null == message) {
            message = code;
        }
        return message;
    }
}
