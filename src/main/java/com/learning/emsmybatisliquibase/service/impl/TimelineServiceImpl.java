package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeCycleMapper;
import com.learning.emsmybatisliquibase.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeCycleErrorCodes.EMPLOYEE_CYCLE_NOT_FOUND;

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
}
