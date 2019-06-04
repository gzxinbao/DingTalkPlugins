package com.ipayroll.dingtalk.repository;

import com.ipayroll.dingtalk.entity.annual.AnnualLeaveMessage;
import com.ipayroll.dingtalk.enums.CheckMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @version 1.2
 * @author: lujiakang
 * @date: 2019/5/30
 */
public interface AnnualLeaveMessageRepository extends JpaRepository<AnnualLeaveMessage, Long>, JpaSpecificationExecutor<AnnualLeaveMessage> {

    AnnualLeaveMessage findByCheckMessage(CheckMessage checkMessage);

}
