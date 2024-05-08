package com.learning.emsmybatisliquibase.controller;


import com.learning.emsmybatisliquibase.dto.ApplyAttendanceDto;
import com.learning.emsmybatisliquibase.dto.UpdateAttendanceDto;
import com.learning.emsmybatisliquibase.dto.ViewEmployeeAttendanceDto;
import com.learning.emsmybatisliquibase.entity.Attendance;
import com.learning.emsmybatisliquibase.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/attendance")
@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/apply/{employeeUuid}")
    public ResponseEntity<List<Attendance>> apply(@PathVariable UUID employeeUuid, @RequestBody List<ApplyAttendanceDto> attendanceDto) {
        return new ResponseEntity<>(attendanceService.apply(employeeUuid, attendanceDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{employeeUuid}/attendance/{attendanceUuid}")
    public ResponseEntity<Attendance> update(@PathVariable UUID employeeUuid, @PathVariable UUID attendanceUuid, @RequestBody UpdateAttendanceDto attendanceDto) {
        return new ResponseEntity<>(attendanceService.update(employeeUuid, attendanceUuid, attendanceDto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/{employeeUuid}/attendance/{attendanceUuid}")
    public ResponseEntity<Attendance> get(@PathVariable UUID employeeUuid, @PathVariable UUID attendanceUuid) {
        return new ResponseEntity<>(attendanceService.getByUuid(employeeUuid, attendanceUuid), HttpStatus.OK);
    }

    @GetMapping("/get/{employeeUuid}")
    public ResponseEntity<ViewEmployeeAttendanceDto> getEmployeeAttendance(@PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(attendanceService.getEmployeeAttendance(employeeUuid), HttpStatus.OK);
    }

    @GetMapping("/get/attendance/manager/{managerUuid}")
    public ResponseEntity<List<ViewEmployeeAttendanceDto>> getAllEmployeesAttendanceByManager(@PathVariable UUID managerUuid) {
        return new ResponseEntity<>(attendanceService.getAllEmployeesAttendanceByManager(managerUuid), HttpStatus.OK);
    }

    @GetMapping("/get/attendance/full-team/{employeeUuid}")
    public ResponseEntity<List<ViewEmployeeAttendanceDto>> getFullTeamAttendance(@PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(attendanceService.getFullTeamAttendance(employeeUuid), HttpStatus.OK);
    }
}
