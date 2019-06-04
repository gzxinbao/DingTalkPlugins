package com.ipayroll.dingtalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/27
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "callback")
public class CallbackConfig {

    private String token;

    private String callbackUrl;

    private String corpId;

    private String ascKey;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAscKey() {
        return ascKey;
    }

    public void setAscKey(String ascKey) {
        this.ascKey = ascKey;
    }

}
