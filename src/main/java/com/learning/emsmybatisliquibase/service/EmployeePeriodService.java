package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.entity.EmployeePeriod;

import java.util.List;
import java.util.UUID;

public interface EmployeePeriodService {
    SuccessResponseDto cycleAssignment(List<UUID> employeeIds);

    SuccessResponseDto updateEmployeeCycleStatus(UUID employeeCycleId, PeriodStatus status);

    SuccessResponseDto updateEmployeeCyclesByCycleId(UUID cycleId, PeriodStatus status);

    FullEmployeeCycleDto getEmployeeCycleById(UUID employeeCycleId);

    List<EmployeePeriod> getByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId);
}
