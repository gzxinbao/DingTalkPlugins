package com.ipayroll.dingtalk.service.annual.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.ipayroll.dingtalk.common.URLConstant;
import com.ipayroll.dingtalk.config.AgentConfig;
import com.ipayroll.dingtalk.config.CallbackConfig;
import com.ipayroll.dingtalk.data.entity.ResponseCode;
import com.ipayroll.dingtalk.entity.annual.AnnualLeave;
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveFlow;
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveLog;
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveMessage;
import com.ipayroll.dingtalk.enums.CheckMessage;
import com.ipayroll.dingtalk.enums.CheckType;
import com.ipayroll.dingtalk.enums.SuitePushType;
import com.ipayroll.dingtalk.exception.ServiceException;
import com.ipayroll.dingtalk.repository.AnnualLeaveFlowRepository;
import com.ipayroll.dingtalk.repository.AnnualLeaveLogRepository;
import com.ipayroll.dingtalk.repository.AnnualLeaveMessageRepository;
import com.ipayroll.dingtalk.repository.AnnualLeaveRepository;
import com.ipayroll.dingtalk.service.annual.AccessTokenUtil;
import com.ipayroll.dingtalk.service.annual.AnnualLeaveService;
import com.ipayroll.dingtalk.util.*;
import com.ipayroll.dingtalk.view.AnnualLeaveView;
import com.ipayroll.dingtalk.view.UserViewItem;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/22
 */
@Service
public class AnnualLeaveServiceImpl implements AnnualLeaveService {

    private static final Logger logger = LoggerFactory.getLogger(AnnualLeaveServiceImpl.class);

    @Resource
    private CallbackConfig callbackConfig;
    @Resource
    private AgentConfig agentConfig;
    @Resource
    private AnnualLeaveRepository annualLeaveRepository;
    @Resource
    private AnnualLeaveMessageRepository annualLeaveMessageRepository;
    @Resource
    private AnnualLeaveFlowRepository annualLeaveFlowRepository;
    @Resource
    private AnnualLeaveLogRepository annualLeaveLogRepository;
    @Resource
    private AccessTokenUtil accessTokenUtil;

    @Override
    public OapiUserGetuserinfoResponse getUserInfo(String requestAuthCode,String accessToken) {
        //获取用户信息
        DingTalkClient clientUser = new DefaultDingTalkClient(URLConstant.URL_GET_USER_INFO);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(requestAuthCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response;
        try {
            response = clientUser.execute(request, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        return  response;
    }

    @Override
    public void registerCallBack() throws ApiException {
        String accessToken = accessTokenUtil.getToken();
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.REGISTER_CALL_BACK);
        OapiCallBackRegisterCallBackRequest request = new OapiCallBackRegisterCallBackRequest();
        request.setUrl(callbackConfig.getCallbackUrl()+callbackConfig.getCorpId());
        request.setAesKey(callbackConfig.getAscKey());
        request.setToken(callbackConfig.getToken());
        List<String> callbackTagList = new ArrayList<>();
        callbackTagList.add(SuitePushType.BPMS_TASK_CHANGE.getKey());
        callbackTagList.add(SuitePushType.BPMS_INSTANCE_CHANGE.getKey());
        request.setCallBackTag(callbackTagList);
        OapiCallBackRegisterCallBackResponse response = client.execute(request,accessToken);
        //回调地址已经存在
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
    }

    @Override
    public Map<String, String> receiveCallBack(String corpId, String signature, String timestamp, String nonce, JSONObject json) {
        Map<String, String> encryptedMap = new HashMap<>();
        try{
            String token = callbackConfig.getToken();
            String aesKey = callbackConfig.getAscKey();

            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(token, aesKey, corpId);
            String encryptMsg = json.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(signature,timestamp,nonce,encryptMsg);

            //具体业务处理,返回给钉钉开放平台返回的明文数据
            String returnStr = isvCallbackEvent(plainText,corpId) ;

            encryptedMap = dingTalkEncryptor.getEncryptedMap(returnStr, System.currentTimeMillis(), com.dingtalk.oapi.lib.aes.Utils.getRandomStr(8));
            return encryptedMap;
        }catch (Exception e){
            logger.info("解密失败程序异常: ");
            logger.info("corpId:"+ corpId);
            logger.info("signature:"+ signature);
            logger.info("timestamp:"+ timestamp);
            logger.info("nonce:"+ nonce);
            logger.info("json:"+ json);
            return encryptedMap;
        }
    }

    @Override
    public JSONObject getCallBack() throws ApiException {
        String accessToken = accessTokenUtil.getToken();
        DingTalkClient  client = new DefaultDingTalkClient(URLConstant.GET_CALL_BACK);
        OapiCallBackGetCallBackRequest request = new OapiCallBackGetCallBackRequest();
        request.setHttpMethod("GET");
        OapiCallBackGetCallBackResponse response =client.execute(request,accessToken);
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        return jsonObject;
    }

    @Override
    public Map<String, Object> getProcessInstance(String processInstanceId) throws ApiException {
        String accessToken = accessTokenUtil.getToken();
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.GET_PROCESSINSTANCE);
        OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
        request.setProcessInstanceId(processInstanceId);
        OapiProcessinstanceGetResponse response = client.execute(request,accessToken);
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }

        //解析body数据
        JSONObject body = JSON.parseObject(response.getBody());
        JSONObject processInstance = body.getJSONObject("process_instance");
        JSONArray formComponentValues = processInstance.getJSONArray("form_component_values");
        Float durationInDay = 0F;
        String tag = "";
        for (int i=0; i<formComponentValues.size(); i++){
            JSONObject pojo = (JSONObject)formComponentValues.get(i);
            String id = pojo.getString("id");
            if ("DDHolidayField-JKDPOJN2".equalsIgnoreCase(id)){
                JSONObject extValue = pojo.getJSONObject("ext_value");
                //Object objectExtension = extValue.get("extension");
                JSONObject extension = extValue.getJSONObject("extension");
                tag = extension.getString("tag");
                //年假事件的unit为DAY
                if (tag.contains("年假")){
                    durationInDay =  Float.parseFloat(extValue.get("durationInDay").toString());
                }
                break;
            }
        }

        //获取发起人id
        String staffId = processInstance.getString("originator_userid");

        //获取审批人id（可能多个）
        JSONArray tasks = processInstance.getJSONArray("tasks");
        List<String> checkIds = new ArrayList<>();
        for (int i=0; i<tasks.size(); i++){
            JSONObject jsonObject = tasks.getJSONObject(i);
            String userId = jsonObject.getString("userid");
            checkIds.add(userId);
        }

        //获取操作类型
        String biz_action = processInstance.getString("biz_action");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("tag", tag);
        resultMap.put("durationInDay", durationInDay);
        resultMap.put("staffId", staffId);
        resultMap.put("checkId", String.join(",", checkIds));
        resultMap.put("biz_action", biz_action);

        return resultMap;
    }

    @Override
    public void updateCallBack() throws ApiException {
        String accessToken = accessTokenUtil.getToken();
        DingTalkClient  client = new DefaultDingTalkClient(URLConstant.UPDATE_CALL_BACK);
        OapiCallBackUpdateCallBackRequest request = new OapiCallBackUpdateCallBackRequest();
        request.setUrl(callbackConfig.getCallbackUrl()+callbackConfig.getCorpId());
        request.setAesKey(callbackConfig.getAscKey());
        request.setToken(callbackConfig.getToken());
        List<String> callbackTagList = new ArrayList<>();
        callbackTagList.add(SuitePushType.BPMS_TASK_CHANGE.getKey());
        callbackTagList.add(SuitePushType.BPMS_INSTANCE_CHANGE.getKey());
        request.setCallBackTag(callbackTagList);
        OapiCallBackUpdateCallBackResponse response = client.execute(request,accessToken);
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
    }

    @Override
    public Map<String, Object> login(String requestAuthCode) {
        String accessToken = accessTokenUtil.getToken();
        OapiUserGetuserinfoResponse response = getUserInfo(requestAuthCode,accessToken);
        //3.查询得到当前用户的userId
        String userId = response.getUserid();
        String userName = getDingDingUser(userId).getName();
        //返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", userId);
        resultMap.put("userName", userName);
        return resultMap;
    }

    @Override
    public AnnualLeaveView getAnnualLeave(String requestAuthCode) {
        String accessToken = accessTokenUtil.getToken();
        OapiUserGetuserinfoResponse response = getUserInfo(requestAuthCode,accessToken);
        String userId = response.getUserid();
        AnnualLeaveView view = getUser(userId);
        return view;
    }

    /**
     * 获取钉钉用户信息
     * @param userId
     * @return
     */
    @Override
    public OapiUserGetResponse getDingDingUser(String userId) {
        try {
            String accessToken = accessTokenUtil.getToken();
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_USER_GET);
            OapiUserGetRequest request = new OapiUserGetRequest();
            request.setUserid(userId);
            request.setHttpMethod("GET");
            OapiUserGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != ResponseCode.SUCCESS){
                throw new ServiceException("用户不存在");
            }
            return response;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 处理各种回调
     * @param callbackMsg
     * @param corpId
     */
    private String isvCallbackEvent(String callbackMsg,String corpId) throws ApiException {
        JSONObject callbackMsgJson = JSONObject.parseObject(callbackMsg);
        String eventType = callbackMsgJson.getString("EventType");
        //默认返回success明文。
        String responseEncryMsg = "success";
        logger.info("eventType: "+eventType);
        if(SuitePushType.BPMS_TASK_CHANGE.getKey().equals(eventType)){
            logger.info("BPMS_TASK_CHANGE: "+callbackMsgJson);
        } else if (SuitePushType.BPMS_INSTANCE_CHANGE.getKey().equals(eventType)){
            logger.info("BPMS_INSTANCE_CHANGE: "+callbackMsgJson);
            //年假处理入口
           handlerCallback(callbackMsgJson);
        } else if (SuitePushType.CHECK_URL.getKey().equals(eventType)){
            logger.info("CHECK_URL: "+callbackMsgJson);
        }else{
            logger.info("corpId", corpId);
            logger.info("callbackMsg", callbackMsg);
            throw new ServiceException("未注册的实例");
        }
        return responseEncryMsg;
    }

    private void handlerCallback(JSONObject callbackMsgJson) throws ApiException {
        String staffId = callbackMsgJson.getString("staffId");
        String processInstanceId = callbackMsgJson.getString("processInstanceId");
        String type = callbackMsgJson.getString("type");
        String result = callbackMsgJson.getString("result");
        String url = callbackMsgJson.getString("url");
        String title = callbackMsgJson.getString("title");
        Map<String, Object> mapResult = getProcessInstance(processInstanceId);
        //年假天数
        Float durationInDay = (Float) mapResult.get("durationInDay");
        String checkIds = (String)mapResult.get("checkId");
        String biz_action = (String)mapResult.get("biz_action");
        String tag = (String)mapResult.get("tag");

        //非年假审批事件，跳过
        if (!tag.contains("年假")){
            return;
        }

        Date thisYear = DateUtils.getThisYearFirstDay();
        AnnualLeaveFlow annualLeaveFlowThisYear = annualLeaveFlowRepository.findByUserIdAndYear(staffId,thisYear);
        if (annualLeaveFlowThisYear == null){
            throw new ServiceException("员工id: "+staffId+" 今年数据不存在导致请年假失败，检查数据！");
        }
        //今年剩余年假
        float daysThisYear = annualLeaveFlowThisYear.getTotalDays() - annualLeaveFlowThisYear.getPassDays() > 0f ? annualLeaveFlowThisYear.getTotalDays() - annualLeaveFlowThisYear.getPassDays() : 0f;

        Date lastYear = DateUtils.getLastYearFirstDay();
        AnnualLeaveFlow annualLeaveFlowLastYear = annualLeaveFlowRepository.findByUserIdAndYear(staffId,lastYear);
        if (annualLeaveFlowLastYear == null){
            throw new ServiceException("员工id: "+staffId+" 去年数据不存在导致请年假失败，检查数据！");
        }
        //去年剩余年假
        float daysLastYear = annualLeaveFlowLastYear.getTotalDays() - annualLeaveFlowLastYear.getPassDays() > 0f ? annualLeaveFlowLastYear.getTotalDays() - annualLeaveFlowLastYear.getPassDays() : 0f;

        //剩余年假总数
        float days = daysThisYear + daysLastYear;

        /**
         * 操作日志记录
         */
        AnnualLeaveLog annualLeaveLog = new AnnualLeaveLog();
        annualLeaveLog.setUserId(staffId);
        annualLeaveLog.setTitle(title);
        annualLeaveLog.setDurationInDay(durationInDay);
        annualLeaveLog.setDaysThisYear(daysThisYear);
        annualLeaveLog.setDaysLastYear(daysLastYear);

        logger.info("type: "+type);
        logger.info("result: "+result);
        logger.info("biz_action: "+biz_action);

        // 提交审核事件，计算剩余年假，年假不足则消息通知用户和审批人
        if (CheckType.START.getName().equalsIgnoreCase(type)){
            //非撤销操作，年假不足
            if (!CheckType.REVOKE.getName().equalsIgnoreCase(biz_action) && (durationInDay.compareTo(days) == 1)) {
                annualLeaveLog.setCheckType(CheckType.NOT_ENOUGH);
                annualLeaveLogRepository.save(annualLeaveLog);

                AnnualLeaveMessage annualLeaveMessageCommitter = annualLeaveMessageRepository.findByCheckMessage(CheckMessage.COMMITTER_ANNUAL);
                sendMessage(staffId, annualLeaveMessageCommitter.getContent(), url);

                AnnualLeaveMessage annualLeaveMessageChecker = annualLeaveMessageRepository.findByCheckMessage(CheckMessage.CHECKER_ANNUAL);
                Map<String, String> map = new HashMap<>();
                map.put("userName", getDingDingUser(staffId).getName());
                String content = PlaceholderUtils.resolvePlaceholders(annualLeaveMessageChecker.getContent(), map);
                sendMessage(checkIds, content, url);
            }
        }
        //审核结束事件
        if (CheckType.FINISH.getName().equalsIgnoreCase(type)){
            //通过
            if (CheckType.AGREE.getName().equalsIgnoreCase(result)){
                //审批通过后，撤销，需恢复数据
                if (CheckType.REVOKE.getName().equalsIgnoreCase(biz_action)){
                    annualLeaveLog.setCheckType(CheckType.REVOKE);

                    annualLeaveFlowLastYear.setPassDays(annualLeaveFlowLastYear.getPassDaysLast());
                    annualLeaveFlowThisYear.setPassDays(annualLeaveFlowThisYear.getPassDaysLast());
                    annualLeaveFlowThisYear.setPassDaysLast(0F);
                    annualLeaveFlowLastYear.setPassDaysLast(0F);
                }else {
                    annualLeaveLog.setCheckType(CheckType.AGREE);

                    //保存这次请假天数，若有撤销则用到
                    annualLeaveFlowLastYear.setPassDaysLast(annualLeaveFlowLastYear.getPassDays());
                    annualLeaveFlowThisYear.setPassDaysLast(annualLeaveFlowThisYear.getPassDays());
                    //审批通过, 年假天数扣减
                    if (daysLastYear >= durationInDay){
                        annualLeaveFlowLastYear.setPassDays(annualLeaveFlowLastYear.getPassDays()+durationInDay);
                    }else {
                        annualLeaveFlowLastYear.setPassDays(annualLeaveFlowLastYear.getTotalDays());
                        annualLeaveFlowThisYear.setPassDays(annualLeaveFlowThisYear.getPassDays() + durationInDay - daysLastYear);
                    }
                }
            }
            //拒绝
            if (CheckType.REFUSE.getName().equalsIgnoreCase(result)){
                annualLeaveLog.setCheckType(CheckType.REFUSE);
            }
            annualLeaveLogRepository.save(annualLeaveLog);
            annualLeaveFlowRepository.save(annualLeaveFlowThisYear);
            annualLeaveFlowRepository.save(annualLeaveFlowLastYear);
        }
    }

    public void sendMessage(String userId,String content,String url) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.SEND_MESSAGE);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userId);
        request.setAgentId(agentConfig.getCheckAgentId());
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("link");
        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
        msg.getLink().setTitle("年假审批");
        msg.getLink().setText(content);
        msg.getLink().setMessageUrl(url);
        msg.getLink().setPicUrl(url);
        request.setMsg(msg);

        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,accessTokenUtil.getToken());
        if (response.getErrcode() != ResponseCode.SUCCESS) {
            throw new ServiceException(response.getMsg());
        }
    }

    @Override
    public List<String> getDepartmentIdList(String parentId) {
        //获取部门列表
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.DEPARTMENT_LIST);
        OapiDepartmentListRequest request = new OapiDepartmentListRequest();
        if (!StringUtils.isEmpty(parentId)){
            request.setId(parentId);
        }
        request.setHttpMethod("GET");
        OapiDepartmentListResponse response = null;
        try {
            response = client.execute(request, accessTokenUtil.getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
        JSONObject body = JSONObject.parseObject(response.getBody());
        JSONArray departmentArray = body.getJSONArray("department");
        List<String> departmentIdList = new ArrayList<>();
        for (int i=0; i<departmentArray.size(); i++){
            JSONObject department = departmentArray.getJSONObject(i);
            String departmentId = department.get("id").toString();
            departmentIdList.add(departmentId);
        }
        return departmentIdList;
    }

    @Override
    public List<String> getAllUserIdList() {
        List<String> departmentIdList = getDepartmentIdList(null);
        //获取用户列表
        List<String> userIdList = new ArrayList<>();
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.USERID_LIST);
        for (String departmentId : departmentIdList){
            OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
            req.setDeptId(departmentId);
            req.setHttpMethod("GET");
            OapiUserGetDeptMemberResponse rsp = null;
            try {
                rsp = client.execute(req, accessTokenUtil.getToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
            if (rsp.getErrcode() == ResponseCode.ERROR){
                throw new ServiceException(rsp.getMsg());
            }
            JSONObject body = JSONObject.parseObject(rsp.getBody());
            JSONArray userIds = body.getJSONArray("userIds");
            for (int i=0; i<userIds.size(); i++){
                String userId = userIds.getString(i);
                userIdList.add(userId);
            }
        }
        return userIdList;
    }

    @Override
    public List<UserViewItem> getAllUserList() {
        List<UserViewItem> userViewItems = new ArrayList<>();
        List<AnnualLeave> annualLeaves = annualLeaveRepository.findAll();
        for (AnnualLeave annualLeave : annualLeaves){
            UserViewItem userViewItem = new UserViewItem();
            userViewItem.setUserId(annualLeave.getUserId());
            userViewItem.setUserName(annualLeave.getUserName());
            userViewItems.add(userViewItem);
        }
        return userViewItems;
    }

    @Override
    public AnnualLeaveView getUser(String userId) {
        AnnualLeave annualLeave = annualLeaveRepository.findByUserId(userId);
        if (annualLeave == null){
            throw new ServiceException("您的钉钉数据尚未同步下来，请联系管理员！");
        }
        AnnualLeaveView view = BeanUtils.copy(annualLeave, AnnualLeaveView.class);
        String userName = annualLeave.getUserName();
        if (!StringUtils.isEmpty(userName)){
            String lastName = userName.substring(1);
            view.setLastName(lastName);
        }

        Date thisYear = DateUtils.getThisYearFirstDay();
        AnnualLeaveFlow annualLeaveFlowThisYear = annualLeaveFlowRepository.findByUserIdAndYear(userId,thisYear);
        float daysThisYear = annualLeaveFlowThisYear.getTotalDays() - annualLeaveFlowThisYear.getPassDays() > 0f ? annualLeaveFlowThisYear.getTotalDays() - annualLeaveFlowThisYear.getPassDays() : 0f;
        view.setDays(daysThisYear);

        Date lastYear = DateUtils.getLastYearFirstDay();
        AnnualLeaveFlow annualLeaveFlowLastYear = annualLeaveFlowRepository.findByUserIdAndYear(userId,lastYear);
        float daysLastYear = annualLeaveFlowLastYear.getTotalDays() - annualLeaveFlowLastYear.getPassDays() > 0f ? annualLeaveFlowLastYear.getTotalDays() - annualLeaveFlowLastYear.getPassDays() : 0f;
        view.setDaysLastYear(daysLastYear);

        return view;
    }
}
