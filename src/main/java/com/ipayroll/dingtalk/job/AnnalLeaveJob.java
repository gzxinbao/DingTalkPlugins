package com.ipayroll.dingtalk.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSmartworkHrmEmployeeListRequest;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeListResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.ipayroll.dingtalk.common.URLConstant;
import com.ipayroll.dingtalk.data.entity.ResponseCode;
import com.ipayroll.dingtalk.entity.annual.AnnualLeave;
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveFlow;
import com.ipayroll.dingtalk.exception.ServiceException;
import com.ipayroll.dingtalk.repository.AnnualLeaveFlowRepository;
import com.ipayroll.dingtalk.repository.AnnualLeaveRepository;
import com.ipayroll.dingtalk.service.annual.AnnualLeaveService;
import com.ipayroll.dingtalk.service.annual.AccessTokenUtil;
import com.ipayroll.dingtalk.util.DateUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/3
 */
@Component
@Slf4j
public class AnnalLeaveJob {

    private static final Logger logger = LoggerFactory.getLogger(AnnalLeaveJob.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final String SYS_TIME = "0 0 3 * * ?";
    private static final String YEAR_TIME = "0 0 1 1 1 ?";

    @Resource
    private AnnualLeaveService annualLeaveService;
    @Resource
    private AnnualLeaveRepository annualLeaveRepository;
    @Resource
    private AnnualLeaveFlowRepository annualLeaveFlowRepository;
    @Resource
    private AccessTokenUtil accessTokenUtil;

    /**
     * 每天凌晨3点同步钉钉用户数据
     */
    @Scheduled(cron = SYS_TIME)
    public void synDataJob() throws ParseException {
        List<String> userIdList = annualLeaveService.getAllUserIdList();
        logger.info("=================== 同步数据开始 ====================");
        for (String userId : userIdList){
            Map<String, String> smartMap = getSmartWorkHrmEmployee(userId);
            String userName = smartMap.get("userName");
            String confirmJoinTime = smartMap.get("confirmJoinTime");
            String joinWorkingTime = smartMap.get("joinWorkingTime");
            String regularTime = smartMap.get("regularTime");
            if (StringUtils.isEmpty(regularTime)){
                logger.error(userName+"在钉钉上转正日期未设置，请先联系管理员设置！");
                continue;
            }
            if (StringUtils.isEmpty(joinWorkingTime)){
                logger.error(userName+"在钉钉上首次工作时间未设置，请先联系管理员设置！");
                continue;
            }
            if (StringUtils.isEmpty(confirmJoinTime)){
                logger.error(userName+"在钉钉上入职时间未设置，请先联系管理员设置！");
                continue;
            }

            float totalDays = 0F;
            Date regularDate = sdf.parse(regularTime);
            Date nowDate = new Date();
            //已转正
            if (regularDate.before(nowDate)){
                totalDays = DateUtils.calculationAnnualLeave(confirmJoinTime,joinWorkingTime);
            }

            //基本数据维护，空为新员工
            AnnualLeave annualLeave = annualLeaveRepository.findByUserId(userId);
            if (annualLeave == null){
                annualLeave = new AnnualLeave();
            }
            annualLeave.setUserId(userId);
            annualLeave.setUserName(userName);
            annualLeave.setConfirmJoinTime(sdf.parse(confirmJoinTime));
            annualLeave.setJoinWorkingTime(sdf.parse(joinWorkingTime));
            annualLeave.setRegularTime(sdf.parse(regularTime));
            OapiUserGetResponse response = annualLeaveService.getDingDingUser(userId);
            annualLeave.setIsAdmin(response.getIsAdmin());
            annualLeaveRepository.save(annualLeave);

            //维护今年年假数据
            Date thisYear = DateUtils.getThisYearFirstDay();
            AnnualLeaveFlow annualLeaveFlow = annualLeaveFlowRepository.findByUserIdAndYear(userId,thisYear);
            if (annualLeaveFlow == null){
                annualLeaveFlow = new AnnualLeaveFlow();
                annualLeaveFlow.setPassDays(0F);
                annualLeaveFlow.setPassDaysLast(0F);
            }
            annualLeaveFlow.setUserId(userId);
            annualLeaveFlow.setYear(thisYear);
            annualLeaveFlow.setTotalDays(totalDays);
            annualLeaveFlowRepository.save(annualLeaveFlow);

            //维护去年数据，已存在数据不可更改
            Date lastYear = DateUtils.getLastYearFirstDay();
            AnnualLeaveFlow annualLeaveFlowLast = annualLeaveFlowRepository.findByUserIdAndYear(userId,lastYear);
            if (annualLeaveFlowLast == null){
                annualLeaveFlowLast = new AnnualLeaveFlow();
                annualLeaveFlowLast.setPassDays(totalDays);
                annualLeaveFlowLast.setUserId(userId);
                annualLeaveFlowLast.setYear(lastYear);
                annualLeaveFlowLast.setTotalDays(totalDays);
                annualLeaveFlowLast.setPassDaysLast(0F);
                annualLeaveFlowRepository.save(annualLeaveFlowLast);
            }
            logger.info(userName+"同步数据成功！");
        }
        logger.info("=================== 同步数据结束 ====================");
    }


    /**
     * 获取花名册信息
     * @param userId
     */
    public Map<String, String> getSmartWorkHrmEmployee(String userId){
        String accessToken = accessTokenUtil.getToken();

        //获取员工花名册信息
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.SMARTWORK_HRM_EMPLOYEE);
        OapiSmartworkHrmEmployeeListRequest req = new OapiSmartworkHrmEmployeeListRequest();
        req.setUseridList(userId);
        /**
         * sys00-name 姓名
         * sys00-confirmJoinTime 入职时间
         * sys01-probationPeriodType 试用期
         * sys01-regularTime 转正时间
         * sys02-joinWorkingTime 首次参加工作时间
         */
        req.setFieldFilterList("sys00-name,sys00-confirmJoinTime,sys01-probationPeriodType,sys01-regularTime,sys02-joinWorkingTime");
        OapiSmartworkHrmEmployeeListResponse rsp;
        try {
            rsp = client.execute(req, accessToken);
            if (rsp.getErrcode() != ResponseCode.SUCCESS){
                throw new ServiceException(rsp.getMsg());
            }
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(rsp.getBody());
        JSONArray result = jsonObject.getJSONArray("result");
        JSONObject resultObject = (JSONObject)result.get(0);
        JSONArray fieldList = resultObject.getJSONArray("field_list");

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("userName", "");
        resultMap.put("regularTime", "");
        resultMap.put("confirmJoinTime", "");
        resultMap.put("joinWorkingTime", "");
        for (int i=0; i<fieldList.size();i++ ) {
            JSONObject fieldObject = (JSONObject) fieldList.get(i);
            String field_code = fieldObject.get("field_code").toString();
            Object value = fieldObject.get("value");
            switch (field_code) {
                case "sys00-name":
                    if (value != null){
                        resultMap.put("userName", value.toString());
                    }
                    break;
                case "sys01-regularTime":
                    if (value != null){
                        resultMap.put("regularTime", value.toString());
                    }
                    break;
                case "sys00-confirmJoinTime":
                    if (value != null){
                        resultMap.put("confirmJoinTime", value.toString());
                    }
                    break;
                case "sys02-joinWorkingTime":
                    if (value != null){
                        resultMap.put("joinWorkingTime", value.toString());
                    }
                    break;
                default:
                    break;
            }
        }
        return resultMap;
    }

    /**
     * 每年一月一号凌晨1点，删除前年年假数据，并新增一条新的今年的
     */
    @Scheduled(cron = YEAR_TIME)
    @Transactional(rollbackFor = RuntimeException.class)
    public void yearInitDataJob() {
        List<AnnualLeave> annualLeaves = annualLeaveRepository.findAll();
        for (AnnualLeave annualLeave : annualLeaves){
            String userId = annualLeave.getUserId();
            Map<String, String> smartMap = getSmartWorkHrmEmployee(userId);
            String joinWorkingTime = smartMap.get("joinWorkingTime");
            String regularTime = smartMap.get("regularTime");
            String confirmJoinTime = smartMap.get("confirmJoinTime");
            float totalDays = 0F;
            try {
                Date regularDate = sdf.parse(regularTime);
                Date nowDate = new Date();
                //已转正
                if (regularDate.before(nowDate)){
                    totalDays = DateUtils.calculationAnnualLeave(confirmJoinTime,joinWorkingTime);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //新增一条今年年假数据
            Date thisYear = DateUtils.getThisYearFirstDay();
            AnnualLeaveFlow annualLeaveFlow = annualLeaveFlowRepository.findByUserIdAndYear(userId,thisYear);
            if (annualLeaveFlow == null){
                annualLeaveFlow = new AnnualLeaveFlow();
                annualLeaveFlow.setUserId(userId);
                annualLeaveFlow.setYear(thisYear);
                annualLeaveFlow.setPassDays(0F);
                annualLeaveFlow.setTotalDays(totalDays);
                annualLeaveFlowRepository.save(annualLeaveFlow);
            }

            //删除前年数据
            Date beforeYear = DateUtils.getBeforeYearFirstDay();
            AnnualLeaveFlow annualLeaveFlowBefore = annualLeaveFlowRepository.findByUserIdAndYear(userId,beforeYear);
            if (annualLeaveFlowBefore != null){
                annualLeaveFlowRepository.delete(annualLeaveFlowBefore);
            }
        }
    }
}
