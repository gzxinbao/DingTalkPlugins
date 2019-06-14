package com.ipayroll.dingtalk.entity.annual;

import com.ipayroll.dingtalk.data.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dd_component_annual_leave")
public class AnnualLeave extends BaseEntity {

    /**
     * 钉钉用户id
     */
    @Column(length = 200)
    private String userId;

    /**
     * 钉钉用户名
     */
    @Column(length = 32)
    private String userName;

    /**
     * 入职时间
     */
    @Temporal(TemporalType.DATE)
    private Date confirmJoinTime;

    /**
     * 转正时间
     */
    @Temporal(TemporalType.DATE)
    private Date regularTime;

    /**
     * 首次参加工作时间
     */
    @Temporal(TemporalType.DATE)
    private Date joinWorkingTime;

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

}
