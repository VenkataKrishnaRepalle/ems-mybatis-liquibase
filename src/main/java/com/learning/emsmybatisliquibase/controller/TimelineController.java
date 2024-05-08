package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.EmployeeCycleAndTimelineResponseDto;
import com.learning.emsmybatisliquibase.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @PostMapping("/getActiveTimelineDetails/{employeeId}")
    public ResponseEntity<EmployeeCycleAndTimelineResponseDto> getActiveTimelineDetails(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(timelineService.getActiveTimelineDetails(employeeId), HttpStatus.OK);
    }
}
