package com.ipayroll.dingtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DingtalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingtalkApplication.class, args);
    }

}
