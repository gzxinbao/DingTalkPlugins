package com.ipayroll.dingtalk;

import com.ipayroll.dingtalk.controller.annual.AnnualLeaveController;
import com.ipayroll.dingtalk.job.AnnalLeaveJob;
import com.taobao.api.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DingtalkApplicationTests {

    @Resource
    private AnnualLeaveController indexController;
    @Resource
    private AnnalLeaveJob annalLeaveJob;

    @Test
    public void registerCallBack() {
        indexController.registerCallBack();
    }

    @Test
    public void synDataJob(){
        annalLeaveJob.synDataJob();
    }
}
