package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;

import java.util.List;
import java.util.UUID;

public interface TimelineService {
    FullEmployeeCycleDto getActiveTimelineDetails(UUID employeeId);

    SuccessResponseDto updateTimelineStatus(List<UUID> employeeUuids, ReviewType reviewType, TimelineStatus timelineStatus);

    SuccessResponseDto startTimelinesForQuarter(ReviewType completedReviewType, ReviewType startedReviewType);
}
