package com.ipayroll.dingtalk.enums;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/30
 */
public enum CheckMessage {
    /**年假不足发给审批人的消息**/
    CHECKER_ANNUAL("checker_annual"),
    /** 年假不足发给提交人的消息**/
    COMMITTER_ANNUAL("commiter_annual");


    private final String key;

    CheckMessage(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
