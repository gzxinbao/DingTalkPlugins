package com.ipayroll.dingtalk.view;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.2
 * @author: lujiakang
 * @date: 2019/5/30
 */
@Data
public class AnnualLeaveView implements Serializable {

    /**
     * 钉钉用户id
     */
    private String userId;

    /**
     * 钉钉用户名
     */
    private String userName;

    private String lastName;

    /**
     * 入职时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date confirmJoinTime;

    /**
     * 首次参加工作时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date joinWorkingTime;

    /**
     * 今年剩余年假
     */
    private Float days;

    /**
     * 去年剩余年假
     */
    private Float daysLastYear;

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
}
