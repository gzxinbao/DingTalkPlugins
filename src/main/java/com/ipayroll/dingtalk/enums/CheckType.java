package com.ipayroll.dingtalk.enums;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 审批结果类型
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/5
 */
@JSONType(serializeEnumAsJavaBean = true)
public enum CheckType {

    START("审批事件开始"),
    FINISH("审批事件结束"),
    AGREE("审批通过"),
    REVOKE("撤销"),
    NOT_ENOUGH("年假不足"),
    REFUSE("审批不通过")
    ;

    private String label;

    private String name;

    CheckType(String label) {
        this.label = label;
        this.name = this.name();
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

}
