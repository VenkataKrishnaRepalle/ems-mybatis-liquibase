package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.EmployeeCycleAndTimelineResponseDto;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.CycleService;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import com.learning.emsmybatisliquibase.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final TimelineDao timelineDao;

    private final EmployeeCycleService employeeCycleService;

    private final CycleService cycleService;

    @Override
    public EmployeeCycleAndTimelineResponseDto getActiveTimelineDetails(UUID employeeId) {
        var timeline = timelineDao.getActiveCycleByEmployeeId(employeeId);

        if (timeline == null) {
            throw new NotFoundException("", "Timeline details not found for employee " + employeeId);
        }

        var employeeCycle = employeeCycleService.getEmployeeCycleById(timeline.getEmployeeCycleUuid());
        var cycle = cycleService.getById(employeeCycle.getCycleUuid());

        return EmployeeCycleAndTimelineResponseDto.builder()
                .employeeId(employeeId)
                .employeeCycleId(employeeCycle.getUuid())
                .employeeCycleStatus(employeeCycle.getStatus())
                .cycle(cycle)
                .timeline(timeline)
                .build();
    }
}
