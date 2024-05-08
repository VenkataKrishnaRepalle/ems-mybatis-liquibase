package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.EmployeeCycleAndTimelineResponseDto;

import java.util.UUID;

public interface TimelineService {
    EmployeeCycleAndTimelineResponseDto getActiveTimelineDetails(UUID employeeId);
}
