package com.ipayroll.dingtalk.exception.handler;

import com.alibaba.fastjson.JSON;
import com.ipayroll.dingtalk.data.entity.Constants;
import com.ipayroll.dingtalk.data.entity.ResponseCode;
import com.ipayroll.dingtalk.data.entity.ResponseEntity;
import com.ipayroll.dingtalk.data.helper.AppHelper;
import com.ipayroll.dingtalk.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * <b>GlobalExceptionHandler</b> is 全局异常统一处理
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/8/30
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private AppHelper appHelper;

    /**
     * 业务异常处理
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler({ServiceException.class})
    public String handlerServiceException(HttpServletRequest request, HttpServletResponse response, ServiceException e, RedirectAttributes redirectAttributes) {

        String errorMsg = "业务异常";
        if (StringUtils.isNotEmpty(e.getMessage())) {
            errorMsg = e.getMessage();
        }
        String targetUrl = e.getTarget();
        if (appHelper.isAjaxType(request)) {

            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();

                ResponseEntity entity = new ResponseEntity();
                if(e.getResponseCode() == null || e.getResponseCode() == 0){
                    entity.setCode(ResponseCode.ERROR);
                }else{
                    entity.setCode(e.getResponseCode());
                }
                // 2017/9/15 处理回调
                entity.setMsg(errorMsg);

                out.write(JSON.toJSONString(entity));
                out.flush();
            } catch (IOException e1) {
            }

            return null;
        } else {
            String url = StringUtils.isNotEmpty(targetUrl) ? targetUrl : "/public/error/error";
            redirectAttributes.addFlashAttribute(Constants.ERROR, errorMsg);
            request.setAttribute("error", errorMsg);
            return url;
        }
    }
}
