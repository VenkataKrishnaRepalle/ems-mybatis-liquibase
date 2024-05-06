package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.service.CycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("cycle")
@RestController
@RequiredArgsConstructor
public class CycleController {

    private final CycleService cycleService;

    @PostMapping("create-cycle/{year}")
    public ResponseEntity<Cycle> createCycle(@PathVariable int year) {
        return new ResponseEntity<>(cycleService.createCycle(year), HttpStatus.CREATED);
    }

    @PostMapping("start-scheduled/{cycleId}")
    public ResponseEntity<SuccessResponseDto> startScheduled(@PathVariable UUID cycleId) {
        return new ResponseEntity<>(cycleService.startScheduled(cycleId), HttpStatus.ACCEPTED);
    }

    @GetMapping("getById/{cycleId}")
    public ResponseEntity<Cycle> getById(@PathVariable UUID cycleId) {
        return new ResponseEntity<>(cycleService.getById(cycleId), HttpStatus.OK);
    }

    @PutMapping("updateStatus/{cycleId}")
    public ResponseEntity<SuccessResponseDto> updateStatus(@PathVariable UUID cycleId, @RequestParam CycleStatus status) {
        return new ResponseEntity<>(cycleService.updateStatus(cycleId, status), HttpStatus.ACCEPTED);
    }

    @GetMapping("getCurrentActiveCycle")
    public ResponseEntity<Cycle> getCurrentActiveCycle() {
        return new ResponseEntity<>(cycleService.getCurrentActiveCycle(), HttpStatus.OK);
    }
}
