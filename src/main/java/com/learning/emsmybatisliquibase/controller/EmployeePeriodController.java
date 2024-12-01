package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
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
import java.util.UUID;

@RequestMapping("api/employeePeriod")
@RestController
@RequiredArgsConstructor
public class EmployeePeriodController {

    private final EmployeePeriodService employeePeriodService;

    @PostMapping("employee-cycle-assignment")
    public ResponseEntity<SuccessResponseDto> cycleAssignment(@RequestBody List<UUID> employeeIds) {
        return new ResponseEntity<>(employeePeriodService.cycleAssignment(employeeIds), HttpStatus.CREATED);
    }

    @PutMapping("update-employee-cycle/{employeeCycleId}/status/{status}")
    public ResponseEntity<SuccessResponseDto> updateEmployeeCycleStatus(@PathVariable UUID employeeCycleId, @PathVariable PeriodStatus status) {
        return new ResponseEntity<>(employeePeriodService.updateEmployeeCycleStatus(employeeCycleId, status), HttpStatus.ACCEPTED);
    }

    @GetMapping("getById/{employeeCycleId}")
    public ResponseEntity<FullEmployeeCycleDto> getById(@PathVariable UUID employeeCycleId) {
        return new ResponseEntity<>(employeePeriodService.getEmployeeCycleById(employeeCycleId), HttpStatus.OK);
    }

    @GetMapping("getByCycleId/{employeeId}/cycle/{cycleId}")
    public ResponseEntity<List<EmployeePeriod>> getByCycleId(@PathVariable UUID employeeId, @PathVariable UUID cycleId) {
        return new ResponseEntity<>(employeePeriodService.getByEmployeeIdAndCycleId(employeeId, cycleId), HttpStatus.OK);
    }

}
