package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.CycleStatus;

import java.util.List;
import java.util.UUID;

public interface EmployeeCycleService {
    SuccessResponseDto cycleAssignment(List<UUID> employeeIds);

    SuccessResponseDto updateEmployeeCycleStatus(UUID employeeCycleId, CycleStatus status);

    FullEmployeeCycleDto getEmployeeCycleById(UUID employeeCycleId);
}