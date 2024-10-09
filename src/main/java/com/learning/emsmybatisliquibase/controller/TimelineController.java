package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/getActiveTimelineDetails/{employeeId}")
    public ResponseEntity<FullEmployeeCycleDto> getActiveTimelineDetails(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(timelineService.getActiveTimelineDetails(employeeId), HttpStatus.OK);
    }

    @PutMapping("/updateTimelineStatus")
    public ResponseEntity<SuccessResponseDto> updateTimelineStatus(@RequestBody List<UUID> employeeUuids,
                                                                   @RequestBody ReviewType reviewType,
                                                                   @RequestBody TimelineStatus timelineStatus) {
        timelineService.updateTimelineStatus(employeeUuids, reviewType, timelineStatus);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/startTimeline")
    public ResponseEntity<SuccessResponseDto> updateTimelineForQuarter(@RequestBody ReviewType completedReviewType,
                                                                       @RequestBody ReviewType startReviewType) {
        return new ResponseEntity<>(timelineService.startTimelinesForQuarter(completedReviewType, startReviewType),
                HttpStatus.ACCEPTED);
    }
}
