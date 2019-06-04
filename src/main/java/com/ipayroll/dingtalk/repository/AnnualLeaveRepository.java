package com.ipayroll.dingtalk.repository;

import com.ipayroll.dingtalk.entity.annual.AnnualLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/5/29
 */
public interface AnnualLeaveRepository extends JpaRepository<AnnualLeave, Long>, JpaSpecificationExecutor<AnnualLeave> {

    AnnualLeave findByUserId(String userId);

}
