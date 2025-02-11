package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.service.PeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("api/period")
@RestController
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodService periodService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("create-cycle/{year}")
    public ResponseEntity<Period> createCycle(@PathVariable int year) {
        return new ResponseEntity<>(periodService.createPeriod(year), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("start-scheduled/{periodId}")
    public ResponseEntity<SuccessResponseDto> startScheduled(@PathVariable UUID periodId) {
        return new ResponseEntity<>(periodService.startScheduled(periodId), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("getById/{periodId}")
    public ResponseEntity<Period> getById(@PathVariable UUID periodId) {
        return new ResponseEntity<>(periodService.getById(periodId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("updateStatus/{periodId}")
    public ResponseEntity<SuccessResponseDto> updateStatus(@PathVariable UUID periodId,
                                                           @RequestParam PeriodStatus status) {
        return new ResponseEntity<>(periodService.updateStatus(periodId, status), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("getCurrentActiveCycle")
    public ResponseEntity<Period> getCurrentActiveCycle() {
        return new ResponseEntity<>(periodService.getCurrentActivePeriod(), HttpStatus.OK);
    }
}
