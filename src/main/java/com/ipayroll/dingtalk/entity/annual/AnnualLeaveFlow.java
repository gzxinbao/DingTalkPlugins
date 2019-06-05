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
 * @date: 2019/6/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dd_component_annual_leave_flow")
public class AnnualLeaveFlow extends BaseEntity {

    /**
     * 钉钉用户id
     */
    @Column(length = 200)
    private String userId;

    /**
     * 年标记
     */
    @Temporal(TemporalType.DATE)
    private Date year;

    /**
     * 年假总数
     */
    private Float totalDays;

    /**
     * 已修年假
     */
    private Float passDays;

    /**
     * 上次审核通过的年假记录，撤销申请用到
     */
    private Float passDaysLast;

}
