package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.EmployeeCycle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeCycleDao {

    EmployeeCycle getById(@Param("uuid") UUID uuid);

    int insert(@Param("employeeCycle") EmployeeCycle employeeCycle);

    int update(@Param("employeeCycle") EmployeeCycle employeeCycle);

    List<EmployeeCycle> getByEmployeeIdAndCycleId(@Param("employeeUuid") UUID employeeUuid, @Param("cycleUuid") UUID cycleUuid);

    List<EmployeeCycle> getByStatusAndCycleId(@Param("status") CycleStatus status, @Param("cycleUuid") UUID cycleUuid);

    List<EmployeeCycle> getByEmployeeIdAndStatus(@Param("employeeUuid") UUID employeeUuid, @Param("status") CycleStatus status);

    List<EmployeeCycle> getByEmployeeId(@Param("employeeUuid") UUID employeeUuid);
}
