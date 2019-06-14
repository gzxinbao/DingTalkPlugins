package com.ipayroll.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.ipayroll.dingtalk.controller.annual.AnnualLeaveController;
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveMessage;
import com.ipayroll.dingtalk.enums.CheckMessage;
import com.ipayroll.dingtalk.job.AnnalLeaveJob;
import com.ipayroll.dingtalk.repository.AnnualLeaveMessageRepository;
import com.ipayroll.dingtalk.service.annual.impl.AnnualLeaveServiceImpl;
import com.ipayroll.dingtalk.service.annual.AccessTokenUtil;
import com.taobao.api.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DingTalkApplicationTests {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private AnnualLeaveController indexController;
    @Resource
    private AnnalLeaveJob annalLeaveJob;
    @Resource
    private AnnualLeaveMessageRepository annualLeaveMessageRepository;
    @Resource
    private AnnualLeaveServiceImpl annualLeaveServiceimpl;
    @Resource
    private AccessTokenUtil accessTokenUtil;

    @Test
    public void registerCallBack() throws ApiException {
        indexController.registerCallBack();
    }

    @Test
    public void synDataJob() throws ParseException {
        annalLeaveJob.synDataJob();
    }

    @Test
    public void test() throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
        OapiMessageCorpconversationGetsendresultRequest request  = new OapiMessageCorpconversationGetsendresultRequest();
        request.setAgentId(265196732L);
        request.setTaskId(37090568640L);
        OapiMessageCorpconversationGetsendresultResponse response = client.execute(request, accessTokenUtil.getToken());
        System.out.println(response.getBody());
    }

    @Test
    public void sendMessage() throws ApiException {
        AnnualLeaveMessage annualLeaveMessageCommitter = annualLeaveMessageRepository.findByCheckMessage(CheckMessage.COMMITTER_ANNUAL);
        String url = "https://aflow.dingtalk.com/dingtalk/mobile/homepage.htm?corpid=ding9756ae917ae4830f35c2f4657eb6378f&dd_share=false&showmenu=true&dd_progress=false&back=native&procInstId=96caa30a-49bf-4ae4-81ae-bd93969b5d96&taskId=&swfrom=isv&dinghash=approval&dd_from=#approval";
        annualLeaveServiceimpl.sendMessage("144162032021168262",annualLeaveMessageCommitter.getContent(),url);
    }

    @Test
    public void tets(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        Date thisYear = calendar.getTime();
        System.out.println(sdf.format(thisYear));
    }
}
