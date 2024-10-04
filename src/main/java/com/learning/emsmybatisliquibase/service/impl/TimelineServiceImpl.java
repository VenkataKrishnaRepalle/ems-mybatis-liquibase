package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.Timeline;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeCycleMapper;
import com.learning.emsmybatisliquibase.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeCycleErrorCodes.EMPLOYEE_CYCLE_NOT_FOUND;
import static com.learning.emsmybatisliquibase.exception.errorcodes.TimelineErrorCodes.TIMELINE_NOT_UPDATED;

@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final TimelineDao timelineDao;

    private final EmployeeCycleDao employeeCycleDao;

    private final EmployeeCycleMapper employeeCycleMapper;


    @Override
    public FullEmployeeCycleDto getActiveTimelineDetails(UUID employeeId) {
        var employeeCycle = employeeCycleDao.getActiveCycleByEmployeeId(employeeId);
        if (employeeCycle == null) {
            throw new NotFoundException(EMPLOYEE_CYCLE_NOT_FOUND.code(), "Active Employee Cycle not found for employeeId: " + employeeId);
        }
        var fullTimeline = employeeCycleMapper.employeeCycleToFullEMployeeCycleDto(employeeCycle);
        fullTimeline.setTimelines(timelineDao.getByEmployeeCycleId(employeeCycle.getUuid()));
        return fullTimeline;
    }

    @Override
    public SuccessResponseDto updateTimelineStatus(List<UUID> employeeUuids, ReviewType reviewType, TimelineStatus timelineStatus) {
        var timelines = timelineDao.getByEmployeeUuidsAndReviewType(employeeUuids, reviewType);

        timelines.forEach(timeline -> {
            timeline.setStatus(timelineStatus);
            update(timeline);
        });

        return SuccessResponseDto.builder()
                .data(UUID.randomUUID().toString())
                .success(Boolean.TRUE)
                .build();
    }

    private void update(Timeline timeline) {
        try {
            if (0 == timelineDao.update(timeline)) {
                throw new IntegrityException(TIMELINE_NOT_UPDATED.code(), "Timeline not updated for id: " + timeline.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(TIMELINE_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
    }
}
