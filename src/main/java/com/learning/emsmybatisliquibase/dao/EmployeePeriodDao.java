package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.EmployeePeriod;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeePeriodDao {

    EmployeePeriod getById(@Param("uuid") UUID uuid);

    int insert(@Param("employeePeriod") EmployeePeriod employeePeriod);

    int update(@Param("employeePeriod") EmployeePeriod employeePeriod);

    EmployeePeriod getByEmployeeIdAndPeriodId(@Param("employeeUuid") UUID employeeUuid, @Param("periodUuid") UUID cycleUuid);

    List<EmployeePeriod> getByStatusAndPeriodId(@Param("status") PeriodStatus status, @Param("periodUuid") UUID cycleUuid);

    List<EmployeePeriod> getByEmployeeIdAndStatus(@Param("employeeUuid") UUID employeeUuid, @Param("status") PeriodStatus status);

    List<EmployeePeriod> getByEmployeeId(@Param("employeeUuid") UUID employeeUuid);

    EmployeePeriod getActivePeriodByEmployeeId(@Param("employeeUuid") UUID employeeUuid);

}
