package com.ipayroll.dingtalk.common;

public class URLConstant {
    /**
     * 钉钉网关gettoken地址
     */
    public static final String URL_GET_TOKKEN = "https://oapi.dingtalk.com/gettoken";

    /**
     *获取用户在企业内userId的接口URL
     */
    public static final String URL_GET_USER_INFO = "https://oapi.dingtalk.com/user/getuserinfo";

    /**
     *获取用户接口url
     */
    public static final String URL_USER_GET = "https://oapi.dingtalk.com/user/get";

    /**
     * 获取员工花名册接口url
     */
    public static final String SMARTWORK_HRM_EMPLOYEE = "https://oapi.dingtalk.com/topapi/smartwork/hrm/employee/list";

    /**
     * 注册回调接口url
     */
    public static final String REGISTER_CALL_BACK = "https://oapi.dingtalk.com/call_back/register_call_back";

    /**
     * 获取实例接口url
     */
    public static final String GET_PROCESSINSTANCE = "https://oapi.dingtalk.com/topapi/processinstance/get";

    /**
     * 获取回调接口url
     */
    public static final String GET_CALL_BACK = "https://oapi.dingtalk.com/call_back/get_call_back";

    /**
     * 更新回调接口url
     */
    public static final String UPDATE_CALL_BACK = "https://oapi.dingtalk.com/call_back/update_call_back";

    /**
     * 发送工作消息通知
     */
    public static final String SEND_MESSAGE = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    /**
     * 获取部门列表
     */
    public static final String DEPARTMENT_LIST = "https://oapi.dingtalk.com/department/list";

    /**
     * 根据部门获取用户id列表
     */
    public static final String USERID_LIST = "https://oapi.dingtalk.com/user/getDeptMember";

}
