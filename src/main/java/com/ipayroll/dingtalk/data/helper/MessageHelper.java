package com.ipayroll.dingtalk.data.helper;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * <p>
 * <b>MessageHelper</b> is 国际化消息
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/5/4
 */
public class MessageHelper {

    private static MessageSource messageSource;

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code
     * @param args
     * @return
     */
    public static String message(String code, Object... args) {
        if (null == messageSource) {
            messageSource = SpringHelper.getBean(MessageSource.class);
        }
        String msg = null;
        try {
            Locale locale = LocaleContextHolder.getLocale();
            msg = messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            msg = code;
        }
        return msg;
    }
}
