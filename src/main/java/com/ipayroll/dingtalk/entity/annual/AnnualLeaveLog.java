package com.ipayroll.dingtalk.entity.annual;

import com.ipayroll.dingtalk.data.entity.BaseEntity;
import com.ipayroll.dingtalk.enums.CheckType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.Year;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dd_component_annual_leave_log")
public class AnnualLeaveLog extends BaseEntity {

    /**
     * 钉钉用户id
     */
    @Column(length = 200)
    private String userId;

    /**
     * 描述
     */
    @Column(length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    private CheckType checkType;

    /**
     * 所请年假天数
     */
    private Float durationInDay;

    /**
     * 今年剩余年假
     */
    private Float daysThisYear;

    /**
     * 去年剩余年假
     */
    private Float daysLastYear;

}
