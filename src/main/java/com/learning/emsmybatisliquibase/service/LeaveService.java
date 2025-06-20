package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.ApplyLeaveDto;
import com.learning.emsmybatisliquibase.dto.ParseLeaveRequestDto;
import com.learning.emsmybatisliquibase.dto.ParseLeaveResponseDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.dto.UpdateLeaveByManagerDto;
import com.learning.emsmybatisliquibase.dto.ViewEmployeeLeavesDto;
import com.learning.emsmybatisliquibase.entity.Leave;
import com.learning.emsmybatisliquibase.entity.enums.LeaveStatus;

import java.util.List;
import java.util.UUID;

public interface LeaveService {
    List<ParseLeaveResponseDto> parseLeave(ParseLeaveRequestDto parseLeaveDto);

    List<Leave> applyLeave(UUID employeeId, List<ApplyLeaveDto> applyLeaveDto);

    ViewEmployeeLeavesDto getLeavesByEmployeeId(UUID employeeId);

    List<ViewEmployeeLeavesDto> getAllEmployeesLeavesByManager(UUID managerUuid);

    SuccessResponseDto updateLeavesByManager(UUID managerUuid, LeaveStatus status, List<UpdateLeaveByManagerDto> leavesDto);
}
