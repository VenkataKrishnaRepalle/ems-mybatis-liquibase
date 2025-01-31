package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.EmployeeCycleAndTimelineResponseDto;
import com.learning.emsmybatisliquibase.dto.FullEmployeePeriodDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.EmployeePeriod;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.service.EmployeePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("api/employeePeriod")
@RestController
@RequiredArgsConstructor
public class EmployeePeriodController {

    private final EmployeePeriodService employeePeriodService;

    @PostMapping("employee-period-assignment")
    public ResponseEntity<SuccessResponseDto> cycleAssignment(@RequestBody List<UUID> employeeIds) {
        return new ResponseEntity<>(employeePeriodService.periodAssignment(employeeIds), HttpStatus.CREATED);
    }

    @PutMapping("update-employee-period/{employeePeriodId}/status/{status}")
    public ResponseEntity<SuccessResponseDto> updateEmployeeCycleStatus(@PathVariable UUID employeePeriodId,
                                                                        @PathVariable PeriodStatus status) {
        return new ResponseEntity<>(employeePeriodService.updateEmployeePeriodStatus(employeePeriodId, status),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("getById/{employeePeriodId}")
    public ResponseEntity<FullEmployeePeriodDto> getById(@PathVariable UUID employeePeriodId) {
        return new ResponseEntity<>(employeePeriodService.getEmployeePeriodById(employeePeriodId),
                HttpStatus.OK);
    }

    @GetMapping("getByPeriodId/{employeeId}/cycle/{periodId}")
    public ResponseEntity<List<EmployeePeriod>> getByPeriodId(@PathVariable UUID employeeId,
                                                              @PathVariable UUID periodId) {
        return new ResponseEntity<>(employeePeriodService.getByEmployeeIdAndPeriodId(employeeId, periodId),
                HttpStatus.OK);
    }

    @GetMapping("getAll/{employeeId}")
    public ResponseEntity<Map<String, EmployeeCycleAndTimelineResponseDto>> getAll(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(employeePeriodService.getAll(employeeId), HttpStatus.OK);
    }

}
