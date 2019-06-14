package com.ipayroll.dingtalk.controller.annual;

import com.alibaba.fastjson.JSONObject;
import com.ipayroll.dingtalk.data.controller.BaseController;
import com.ipayroll.dingtalk.job.AnnalLeaveJob;
import com.ipayroll.dingtalk.service.annual.AnnualLeaveService;
import com.ipayroll.dingtalk.view.AnnualLeaveView;
import com.ipayroll.dingtalk.view.UserViewItem;
import com.taobao.api.ApiException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 企业内部E应用
 * @author lujiakang
 * @version 1.0.0
 * @since 2019/5/28
 */
@RestController
public class AnnualLeaveController extends BaseController {

    @Resource
    private AnnualLeaveService indexService;
    @Resource
    private AnnalLeaveJob annalLeaveJob;

    /**
     * 钉钉用户登录，显示当前登录用户的userId和名称
     *
     * @param requestAuthCode 免登临时code
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam(value = "authCode") String requestAuthCode) {
        Map<String, Object> resultMap = indexService.login(requestAuthCode);
        return responseCallback(resultMap);
    }

    /**
     * 查询年假
     * @param requestAuthCode
     * @return
     */
    @RequestMapping(value = "/getAnnualLeave", method = RequestMethod.GET)
    @ResponseBody
    public String getAnnualLeave(@RequestParam(value = "authCode") String requestAuthCode) {
        AnnualLeaveView view = indexService.getAnnualLeave(requestAuthCode);
        return responseCallback(view);
    }

    /**
     * 注册业务事件接口
     * @return
     */
    @RequestMapping(value = "/registerCallBack", method = RequestMethod.POST)
    @ResponseBody
    public String registerCallBack() throws ApiException {
        indexService.registerCallBack();
        return responseCallback("注册成功");
    }

    /**
     * 钉钉注册回调接口,参数和返回类型固定
     * @param corpId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param json
     * @return
     */
    @RequestMapping(value = "/receiveCallBack/{corpId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> receiveCallBack(@PathVariable("corpId") String corpId,
                                                @RequestParam(value = "signature", required = false) String signature,
                                                @RequestParam(value = "timestamp", required = false) String timestamp,
                                                @RequestParam(value = "nonce", required = false) String nonce,
                                                @RequestBody(required = false) JSONObject json
    ){

        Map<String, String> map =  indexService.receiveCallBack(corpId,signature,timestamp,nonce,json);
        return map;
    }

    /**
     * 查询事件回调接口
     * @return
     */
    @RequestMapping(value = "/getCallBack", method = RequestMethod.GET)
    @ResponseBody
    public String getCallBack() throws ApiException {
        JSONObject result =  indexService.getCallBack();
        return responseCallback(result);
    }

    /**
     * 更新事件回调接口
     * @return
     */
    @RequestMapping(value = "/updateCallBack", method = RequestMethod.POST)
    @ResponseBody
    public String updateCallBack() throws ApiException {
        indexService.updateCallBack();
        return responseCallback("更新事件回调接口成功");
    }

    /**
     * 获取审批实例详情
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/getProcessInstance/{processInstanceId}", method = RequestMethod.GET)
    @ResponseBody
    public String getProcessInstance(@PathVariable("processInstanceId") String processInstanceId) throws ApiException {
        Map<String, Object> map =  indexService.getProcessInstance(processInstanceId);
        return responseCallback(map);
    }

    /**
     * 获取部门列表，不传参数表示根部门
     * @param departmentId
     * @return
     */
    @RequestMapping(value = "/getDepartmentIdList", method = RequestMethod.GET)
    @ResponseBody
    public String getDepartmentIdList(@RequestParam("departmentId") String departmentId){
        List<String> result =  indexService.getDepartmentIdList(departmentId);
        return responseCallback(result);
    }

    /**
     * 获取用户id
     * @return
     */
    @RequestMapping(value = "/getAllUserIdList", method = RequestMethod.GET)
    @ResponseBody
    public String getAllUserIdList(){
        List<String> result =  indexService.getAllUserIdList();
        return responseCallback(result);
    }

    /**
     * 获取用户id和姓名
     * @return
     */
    @RequestMapping(value = "/getAllUserList", method = RequestMethod.GET)
    @ResponseBody
    public String getAllUserList(){
        List<UserViewItem> result =  indexService.getAllUserList();
        return responseCallback(result);
    }

    /**
     * 获取用户实例
     * @param userId
     * @return
     */
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @ResponseBody
    public String getUser(@RequestParam(value = "userId") String userId) {
        AnnualLeaveView view = indexService.getUser(userId);
        return responseCallback(view);
    }

    @RequestMapping(value = "/synDataJob", method = RequestMethod.GET)
    @ResponseBody
    public String synDataJob() throws ParseException {
        annalLeaveJob.synDataJob();
        return responseCallback("同步完成");
    }

}


