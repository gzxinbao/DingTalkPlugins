package com.ipayroll.dingtalk.service.annual;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.ipayroll.dingtalk.config.AppConfig;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.ipayroll.dingtalk.common.URLConstant.URL_GET_TOKKEN;

/**
 * 注意正是代码要有异常流处理
 * 获取access_token工具类
 */
@Component
public class AccessTokenUtil {

    private static final Logger bizLogger = LoggerFactory.getLogger(AccessTokenUtil.class);

    @Resource
    private AppConfig  appConfig;

    public String getToken() throws RuntimeException {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URL_GET_TOKKEN);
            OapiGettokenRequest request = new OapiGettokenRequest();

            request.setAppkey(appConfig.getKey());
            request.setAppsecret(appConfig.getSecret());
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            String accessToken = response.getAccessToken();
            return accessToken;
        } catch (ApiException e) {
            bizLogger.error("getAccessToken failed", e);
            throw new RuntimeException();
        }

    }
}
