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
import com.ipayroll.dingtalk.entity.annual.AnnualLeaveMessage;
import com.ipayroll.dingtalk.enums.CheckMessage;
import com.ipayroll.dingtalk.enums.SuitePushType;
import com.ipayroll.dingtalk.exception.ServiceException;
import com.ipayroll.dingtalk.repository.AnnualLeaveMessageRepository;
import com.ipayroll.dingtalk.repository.AnnualLeaveRepository;
import com.ipayroll.dingtalk.service.annual.AnnualLeaveService;
import com.ipayroll.dingtalk.util.*;
import com.ipayroll.dingtalk.view.AnnualLeaveView;
import com.ipayroll.dingtalk.view.UserViewItem;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.rmi.ServerException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public void registerCallBack() {
        String accessToken = AccessTokenUtil.getToken();
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.REGISTER_CALL_BACK);
        OapiCallBackRegisterCallBackRequest request = new OapiCallBackRegisterCallBackRequest();
        request.setUrl(callbackConfig.getCallbackUrl()+callbackConfig.getCorpId());
        request.setAesKey(callbackConfig.getAscKey());
        request.setToken(callbackConfig.getToken());
        List<String> callbackTagList = new ArrayList<>();
        callbackTagList.add(SuitePushType.BPMS_TASK_CHANGE.getKey());
        callbackTagList.add(SuitePushType.BPMS_INSTANCE_CHANGE.getKey());
        request.setCallBackTag(callbackTagList);
        OapiCallBackRegisterCallBackResponse response = null;
        try {
            response = client.execute(request,accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
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
            logger.info("解密之后明文消息: ");
            logger.info("corpId", corpId);
            logger.info("signature", signature);
            logger.info("timestamp", timestamp);
            logger.info("nonce", nonce);
            logger.info("json", json);
            logger.info("plainText", plainText);

            //具体业务处理,返回给钉钉开放平台返回的明文数据
            String returnStr = isvCallbackEvent(plainText,corpId) ;

            encryptedMap = dingTalkEncryptor.getEncryptedMap(returnStr, System.currentTimeMillis(), com.dingtalk.oapi.lib.aes.Utils.getRandomStr(8));
            return encryptedMap;
        }catch (Exception e){
            logger.info("解密失败程序异常: ");
            logger.info("corpId", corpId);
            logger.info("signature", signature);
            logger.info("timestamp", timestamp);
            logger.info("nonce", nonce);
            logger.info("json", json);
            return encryptedMap;
        }
    }

    @Override
    public JSONObject getCallBack() {
        String accessToken = AccessTokenUtil.getToken();
        DingTalkClient  client = new DefaultDingTalkClient(URLConstant.GET_CALL_BACK);
        OapiCallBackGetCallBackRequest request = new OapiCallBackGetCallBackRequest();
        request.setHttpMethod("GET");
        OapiCallBackGetCallBackResponse response = null;
        try {
            response = client.execute(request,accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        return jsonObject;
    }

    @Override
    public Map<String, Object> getProcessInstance(String processInstanceId) {
        String accessToken = AccessTokenUtil.getToken();
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.GET_PROCESSINSTANCE);
        OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
        request.setProcessInstanceId(processInstanceId);
        OapiProcessinstanceGetResponse response = null;
        try {
            response = client.execute(request,accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }

        //解析body数据
        JSONObject body = JSON.parseObject(response.getBody());
        JSONObject processInstance = body.getJSONObject("process_instance");
        JSONArray formComponentValues = processInstance.getJSONArray("form_component_values");
        JSONObject extValue = null;
        Float durationInDay = 0F;
        String tag = "";
        for (int i=0; i<formComponentValues.size(); i++){
            JSONObject pojo = (JSONObject)formComponentValues.get(i);
            Object object = pojo.get("ext_value");
            if (object != null){
                extValue = JSONObject.parseObject(object.toString());
                break;
            }
        }
        if (extValue != null){
            Object object = extValue.get("extension");
            JSONObject extension = JSONObject.parseObject(object.toString());
            tag = extension.get("tag").toString();
            //年假事件的unit为DAY
            if (tag.contains("年假")){
                durationInDay =  Float.parseFloat(extValue.get("durationInDay").toString());
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
    public void updateCallBack() {
        String accessToken = AccessTokenUtil.getToken();
        DingTalkClient  client = new DefaultDingTalkClient(URLConstant.UPDATE_CALL_BACK);
        OapiCallBackUpdateCallBackRequest request = new OapiCallBackUpdateCallBackRequest();
        request.setUrl(callbackConfig.getCallbackUrl()+callbackConfig.getCorpId());
        request.setAesKey(callbackConfig.getAscKey());
        request.setToken(callbackConfig.getToken());
        List<String> callbackTagList = new ArrayList<>();
        callbackTagList.add(SuitePushType.BPMS_TASK_CHANGE.getKey());
        callbackTagList.add(SuitePushType.BPMS_INSTANCE_CHANGE.getKey());
        request.setCallBackTag(callbackTagList);
        OapiCallBackUpdateCallBackResponse response = null;
        try {
            response = client.execute(request,accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (response.getErrcode() != ResponseCode.SUCCESS){
            throw new ServiceException(response.getMsg());
        }
    }

    @Override
    public Map<String, Object> login(String requestAuthCode) {
        String accessToken = AccessTokenUtil.getToken();
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
        String accessToken = AccessTokenUtil.getToken();
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
            String accessToken = AccessTokenUtil.getToken();
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
     * 处理各种回调时间的TAG。这个维度的回调是和套件相关的
     * @param callbackMsg   钉钉开放平台给ISV套件回调的明文数据
     * @param corpId      套件SuiteKey
     */
    private String isvCallbackEvent(String callbackMsg,String corpId) {
        JSONObject callbackMsgJson = JSONObject.parseObject(callbackMsg);
        logger.info("callbackMsgJson: "+callbackMsgJson);
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

    private void handlerCallback(JSONObject callbackMsgJson) {
        String staffId = callbackMsgJson.getString("staffId");
        String processInstanceId = callbackMsgJson.getString("processInstanceId");
        String type = callbackMsgJson.getString("type");
        String result = callbackMsgJson.getString("result");
        Map<String, Object> mapResult = getProcessInstance(processInstanceId);
        //年假天数
        Float durationInDay = (Float) mapResult.get("durationInDay");
        String checkIds = (String)mapResult.get("checkId");
        String biz_action = (String)mapResult.get("biz_action");
        AnnualLeave annualLeave = annualLeaveRepository.findByUserId(staffId);

        //剩余年假天数
        float days = annualLeave.getTotalDays() - annualLeave.getPassDays() > 0f ? annualLeave.getTotalDays() - annualLeave.getPassDays() : 0f;
        // 提交审核事件，计算剩余年假，年假不足则消息通知用户和审批人
        if ("start".equals(type)){
            //非撤销操作
            if (!"REVOKE".equalsIgnoreCase(biz_action)){
                //年假不足
                if (durationInDay.compareTo(days) == 1){
                    AnnualLeaveMessage annualLeaveMessageCommitter = annualLeaveMessageRepository.findByCheckMessage(CheckMessage.COMMITTER_ANNUAL);
                    sendMessage(staffId,annualLeaveMessageCommitter.getContent());

                    AnnualLeaveMessage annualLeaveMessageChecker = annualLeaveMessageRepository.findByCheckMessage(CheckMessage.CHECKER_ANNUAL);
                    Map<String, String> map = new HashMap<>();
                    map.put("userName",getDingDingUser(staffId).getName());
                    String content = PlaceholderUtils.resolvePlaceholders(annualLeaveMessageChecker.getContent(), map);
                    sendMessage(checkIds,content);
                }
            }
        }
        //审核结束事件，通过
        if ("finish".equals(type) && "agree".equals(result)){
            //撤销操作审批通过，需恢复数据
            if ("REVOKE".equalsIgnoreCase(biz_action)){
                annualLeave.setPassDays(annualLeave.getPassDays() - durationInDay);
            }else {
                //审批通过, 年假天数扣减
                annualLeave.setPassDays(annualLeave.getPassDays() + durationInDay);
            }
            annualLeaveRepository.save(annualLeave);
        }
    }

    public void sendMessage(String userId,String content) {
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.SEND_MESSAGE);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userId);
        request.setAgentId(agentConfig.getCheckAgentId());
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(content);
        request.setMsg(msg);

        /*msg.setMsgtype("image");
        msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
        msg.getImage().setMediaId("@lADOdvRYes0CbM0CbA");
        request.setMsg(msg);

        msg.setMsgtype("file");
        msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
        msg.getFile().setMediaId("@lADOdvRYes0CbM0CbA");
        request.setMsg(msg);

        msg.setMsgtype("link");
        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
        msg.getLink().setTitle("test");
        msg.getLink().setText("test");
        msg.getLink().setMessageUrl("test");
        msg.getLink().setPicUrl("test");
        request.setMsg(msg);

        msg.setMsgtype("markdown");
        msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
        msg.getMarkdown().setText("##### text");
        msg.getMarkdown().setTitle("### Title");
        request.setMsg(msg);

        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().getHead().setText("head");
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().getBody().setContent("xxx");
        msg.setMsgtype("oa");
        request.setMsg(msg);

        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle("xxx123411111");
        msg.getActionCard().setMarkdown("### 测试123111");
        msg.getActionCard().setSingleTitle("测试测试");
        msg.getActionCard().setSingleUrl("https://www.baidu.com");
        msg.setMsgtype("action_card");
        request.setMsg(msg);*/

        OapiMessageCorpconversationAsyncsendV2Response response = null;
        try {
           response = client.execute(request,AccessTokenUtil.getToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (response.getErrcode() != ResponseCode.SUCCESS){
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
            response = client.execute(request, AccessTokenUtil.getToken());
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
                rsp = client.execute(req, AccessTokenUtil.getToken());
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
        float days = annualLeave.getTotalDays() - annualLeave.getPassDays() > 0f ? annualLeave.getTotalDays() - annualLeave.getPassDays() : 0f;
        view.setDays(days);
        if (!StringUtils.isEmpty(userName)){
            String lastName = userName.substring(1);
            view.setLastName(lastName);
        }
        return view;
    }
}
