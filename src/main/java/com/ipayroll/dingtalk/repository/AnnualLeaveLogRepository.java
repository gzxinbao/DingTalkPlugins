package com.ipayroll.dingtalk.repository;

import com.ipayroll.dingtalk.entity.annual.AnnualLeaveLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/5
 */
public interface AnnualLeaveLogRepository extends JpaRepository<AnnualLeaveLog, Long>, JpaSpecificationExecutor<AnnualLeaveLog> {
}
