package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Leave;
import com.learning.emsmybatisliquibase.entity.LeaveType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface LeaveDao {
    int insert(@Param("leave") Leave leave);

    int update(@Param("leave") Leave leave);

    int delete(@Param("uuid") UUID uuid);

    List<Leave> getLeavesByEmployee(@Param("employeeUuid") UUID employeeUuid);

    Leave getById(@Param("uuid") UUID uuid);
}
