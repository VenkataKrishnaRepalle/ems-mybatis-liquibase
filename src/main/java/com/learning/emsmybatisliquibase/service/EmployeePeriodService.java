package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.EmployeeCycleAndTimelineResponseDto;
import com.learning.emsmybatisliquibase.dto.FullEmployeePeriodDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.entity.EmployeePeriod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EmployeePeriodService {
    SuccessResponseDto periodAssignment(List<UUID> employeeIds);

    SuccessResponseDto updateEmployeePeriodStatus(UUID employeePeriodId, PeriodStatus status);

    SuccessResponseDto updateEmployeePeriodsByPeriodId(UUID periodId, PeriodStatus status);

    FullEmployeePeriodDto getEmployeePeriodById(UUID employeePeriodId);

    List<EmployeePeriod> getByEmployeeIdAndPeriodId(UUID employeeId, UUID periodId);

    Map<String, EmployeeCycleAndTimelineResponseDto> getAll(UUID employeeId);
}
