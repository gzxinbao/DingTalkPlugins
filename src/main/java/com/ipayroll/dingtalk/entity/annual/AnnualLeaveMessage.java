package com.ipayroll.dingtalk.entity.annual;

import com.ipayroll.dingtalk.data.entity.BaseEntity;
import com.ipayroll.dingtalk.enums.CheckMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dd_component_annual_leave_message")
public class AnnualLeaveMessage extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private CheckMessage checkMessage;

    private String content;

}
