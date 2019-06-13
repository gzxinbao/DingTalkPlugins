package com.ipayroll.dingtalk.service.annual;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.ipayroll.dingtalk.view.AnnualLeaveView;
import com.ipayroll.dingtalk.view.UserViewItem;
import com.taobao.api.ApiException;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/22
 */
public interface AnnualLeaveService {

    /**
     * 获取钉钉用户信息
     * @param requestAuthCode
     * @param accessToken
     * @return
     */
    OapiUserGetuserinfoResponse getUserInfo(String requestAuthCode, String accessToken);

    /**
     * 注册回调
     */
    void registerCallBack() throws ApiException;

    /**
     * 接收回调
     * @param corpId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param json
     * @return
     */
    Map<String, String> receiveCallBack(String corpId, String signature, String timestamp, String nonce, JSONObject json);

    /**
     * 查询回调信息
     * @return
     */
    JSONObject getCallBack() throws ApiException;

    /**
     * 获取实例
     * @param processInstanceId
     * @return
     */
    Map<String, Object> getProcessInstance(String processInstanceId) throws ApiException;

    /**
     * 更新回调
     */
    void updateCallBack() throws ApiException;

    /**
     * 登录
     * @param requestAuthCode
     * @return
     */
    Map<String, Object> login(String requestAuthCode);

    /**
     * 根据登录请求码，获取年假等信息
     * @param requestAuthCode
     * @return
     */
    AnnualLeaveView getAnnualLeave(String requestAuthCode);

    /**
     * 获取部门列表
     * @param departmentId
     * @return
     */
    List<String> getDepartmentIdList(String departmentId);

    /**
     * 获取所有用户ID
     * @return
     */
    List<String> getAllUserIdList();

    /**
     * 获取所有用户id和name
     * @return
     */
    List<UserViewItem> getAllUserList();

    /**
     * 根据钉钉用户id获取用户信息
     * @param userId
     * @return
     */
    AnnualLeaveView getUser(String userId);

    /**
     * 根据钉钉用户id获取信息
     * @param userId
     * @return
     */
    OapiUserGetResponse getDingDingUser(String userId);
}
