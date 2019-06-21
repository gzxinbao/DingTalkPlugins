package com.ipayroll.dingtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/23
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class DingTalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingTalkApplication.class, args);
    }

}
