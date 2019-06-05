package com.ipayroll.dingtalk.repository;

import com.ipayroll.dingtalk.entity.annual.AnnualLeaveFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/4
 */
public interface AnnualLeaveFlowRepository extends JpaRepository<AnnualLeaveFlow, Long>, JpaSpecificationExecutor<AnnualLeaveFlow> {

    @Query(nativeQuery = true,value = "SELECT * FROM dd_component_annual_leave_flow WHERE user_id = :userId AND `year` = DATE_FORMAT(:year,'%Y-%m-%d')")
    AnnualLeaveFlow findByUserIdAndYear(@Param("userId") String userId, @Param("year") Date year);


}
