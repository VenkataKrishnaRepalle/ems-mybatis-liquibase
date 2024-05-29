package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;

import java.util.UUID;

public interface TimelineService {
    FullEmployeeCycleDto getActiveTimelineDetails(UUID employeeId);
}
