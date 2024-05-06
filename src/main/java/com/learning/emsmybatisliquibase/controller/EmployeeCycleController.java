package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
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

@RequestMapping("employeeCycle")
@RestController
@RequiredArgsConstructor
public class EmployeeCycleController {

    private final EmployeeCycleService employeeCycleService;

    @PostMapping("employee-cycle-assignment")
    public ResponseEntity<SuccessResponseDto> cycleAssignment(@RequestBody List<UUID> employeeIds) {
        return new ResponseEntity<>(employeeCycleService.cycleAssignment(employeeIds), HttpStatus.CREATED);
    }

    @PutMapping("update-employee-cycle/{employeeCycleId}/status/{status}")
    public ResponseEntity<SuccessResponseDto> updateEmployeeCycleStatus(@PathVariable UUID employeeCycleId, @PathVariable CycleStatus status) {
        return new ResponseEntity<>(employeeCycleService.updateEmployeeCycleStatus(employeeCycleId, status), HttpStatus.ACCEPTED);
    }

    @GetMapping("getEmployeeCycleById/{employeeCycleId}")
    public ResponseEntity<FullEmployeeCycleDto> getEmployeeCycleById(@PathVariable UUID employeeCycleId) {
        return new ResponseEntity<>(employeeCycleService.getEmployeeCycleById(employeeCycleId), HttpStatus.OK);
    }

}
