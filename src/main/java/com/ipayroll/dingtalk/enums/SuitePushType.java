package com.ipayroll.dingtalk.enums;

/**
 * 套件相关回调枚举
 * Created by 浩倡 on 16-1-17.
 */
public enum SuitePushType {
    /**校验url**/
    CHECK_URL("check_url"),
    /** 审批任务**/
    BPMS_TASK_CHANGE("bpms_task_change"),
    /** 审批实例 **/
    BPMS_INSTANCE_CHANGE("bpms_instance_change");


    private final String key;

    SuitePushType(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}