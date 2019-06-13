package com.ipayroll.dingtalk;

import com.ipayroll.dingtalk.service.annual.AnnualLeaveService;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DingtalkApplication implements ApplicationRunner {

    @Autowired
    AnnualLeaveService annualLeaveService;

    public static void main(String[] args) {
        SpringApplication.run(DingtalkApplication.class, args);
    }

    /**
     * 启动时注册回调，若回调存在则更新
     * 因为钉钉上一个企业只能对应一个回调地址，为了防止开发和线上改乱了，不用每次都去手动更新回调接口
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) throws ApiException {
        annualLeaveService.registerCallBack();
    }
}
