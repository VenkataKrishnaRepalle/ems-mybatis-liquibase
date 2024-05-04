package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.ApplyAttendanceDto;
import com.learning.emsmybatisliquibase.dto.UpdateAttendanceDto;
import com.learning.emsmybatisliquibase.dto.ViewEmployeeAttendanceDto;
import com.learning.emsmybatisliquibase.entity.Attendance;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {
    List<Attendance> apply(UUID employeeUuid, List<ApplyAttendanceDto> attendanceDto);

    Attendance update(UUID employeeUuid, UUID attendanceUuid, UpdateAttendanceDto attendanceDto);

    Attendance getByUuid(UUID employeeUuid, UUID attendanceUuid);

    ViewEmployeeAttendanceDto getEmployeeAttendance(UUID employeeUuid);

    List<ViewEmployeeAttendanceDto> getAllEmployeesAttendanceByManager(UUID managerUuid);

    List<ViewEmployeeAttendanceDto> getFullTeamAttendance(UUID employeeUuid);
}
