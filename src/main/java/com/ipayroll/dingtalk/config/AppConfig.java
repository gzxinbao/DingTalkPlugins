package com.ipayroll.dingtalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/5
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String key;

    private String secret;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
