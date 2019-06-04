package com.ipayroll.dingtalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/29
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "agent")
public class AgentConfig {

    private Long checkAgentId;

    public Long getCheckAgentId() {
        return checkAgentId;
    }

    public void setCheckAgentId(Long checkAgentId) {
        this.checkAgentId = checkAgentId;
    }
}
