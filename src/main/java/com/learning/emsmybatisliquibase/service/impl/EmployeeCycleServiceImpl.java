package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.CycleDao;
import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.EmployeeCycle;
import com.learning.emsmybatisliquibase.entity.Timeline;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeCycleMapper;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeCycleServiceImpl implements EmployeeCycleService {

    private final EmployeeCycleDao employeeCycleDao;

    private final CycleDao cycleDao;

    private final EmployeeDao employeeDao;

    private final TimelineDao timelineDao;

    private final EmployeeCycleMapper employeeCycleMapper;


    @Override
    public SuccessResponseDto cycleAssignment(List<UUID> employeeIds) {
        var cycle = cycleDao.getByStatus(CycleStatus.STARTED);

        for (var employeeId : employeeIds) {
            handleEmployeeCycleAssignment(employeeId, cycle);
        }

        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(UUID.randomUUID()))
                .build();
    }

    private void handleEmployeeCycleAssignment(UUID employeeId, Cycle cycle) {
        var employee = employeeDao.get(employeeId);
        if (employee != null) {
            if (!employeeCycleDao.getByEmployeeIdAndCycleId(employeeId, cycle.getUuid()).isEmpty()) {
                return;
            }

            var employeeCycles = employeeCycleDao.getByEmployeeId(employeeId);
            employeeCycles.stream()
                    .filter(employeeCycle -> employeeCycle.getStatus().equals(CycleStatus.STARTED))
                    .forEach(employeeCycle -> {
                        if (employeeCycle.getCycleUuid().equals(cycle.getUuid())) {
                            updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.INACTIVE);
                        } else if (!cycle.getStatus().equals(CycleStatus.SCHEDULED)) {
                            updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.COMPLETED);
                        }
                    });

            var employeeCycle = saveEmployeeCycle(employeeId, cycle);

            var currentMonth = LocalDateTime.now().getMonth();
            var year = cycle.getStartTime().atZone(ZoneId.systemDefault()).getYear();
            var timelines = generateTimelines(employeeCycle, currentMonth, year);
            timelines.forEach(timeline -> {
                if (0 == timelineDao.insert(timeline)) {
                    throw new IntegrityException("", "");
                }
            });
        }
    }

    private EmployeeCycle saveEmployeeCycle(UUID employeeId, Cycle cycle) {
        var employeeCycle = EmployeeCycle.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employeeId)
                .cycleUuid(cycle.getUuid())
                .status(cycle.getStatus())
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        if (0 == employeeCycleDao.insert(employeeCycle)) {
            throw new IntegrityException("", "");
        }
        return employeeCycle;
    }

    private List<Timeline> generateTimelines(EmployeeCycle employeeCycle, Month currentMonth, int year) {
        var timelines = new ArrayList<Timeline>();
        for (int quarter = 1; quarter <= 4; quarter++) {
            var status = TimelineStatus.SCHEDULED;

            if (quarter == currentMonth.getValue() / 3 + 1) {
                status = TimelineStatus.STARTED;
            } else if (quarter < currentMonth.getValue() / 3 + 1) {
                status = TimelineStatus.LOCKED;
            }

            var startTime = LocalDateTime.of(year, quarter * 3 - 2, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var overdueTime = LocalDateTime.of(year, quarter * 3 - 1, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var lockTime = LocalDateTime.of(year, quarter * 3, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var endDayOfMonth = YearMonth.of(year, quarter * 3).lengthOfMonth();
            var endTime = LocalDateTime.of(year, quarter * 3, endDayOfMonth, 23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

            var timeline = Timeline.builder()
                    .uuid(UUID.randomUUID())
                    .employeeCycleUuid(employeeCycle.getUuid())
                    .type(ReviewType.valueOf("Q" + quarter))
                    .status(status)
                    .startTime(startTime)
                    .overdueTime(overdueTime)
                    .lockTime(lockTime)
                    .endTime(endTime)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();

            timelines.add(timeline);
        }
        return timelines;
    }


    @Override
    public SuccessResponseDto updateEmployeeCycleStatus(UUID employeeCycleId, CycleStatus status) {
        var employeeCycle = getById(employeeCycleId);
        employeeCycle.setStatus(status);
        if (0 == employeeCycleDao.update(employeeCycle)) {
            throw new IntegrityException("", "");
        }

        var timelines = timelineDao.getByEmployeeCycleId(employeeCycleId);
        TimelineStatus timelineStatus;
        switch (status) {
            case STARTED -> timelineStatus = TimelineStatus.STARTED;
            case COMPLETED -> timelineStatus = TimelineStatus.COMPLETED;
            case INACTIVE -> timelineStatus = TimelineStatus.LOCKED;
            default -> timelineStatus = TimelineStatus.NOT_STARTED;
        }
        timelines.forEach(timeline -> {
            timeline.setStatus(timelineStatus);
            timeline.setUpdatedTime(Instant.now());
            if (0 == timelineDao.update(timeline)) {
                throw new IntegrityException("", "");
            }
        });
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(employeeCycleId))
                .build();
    }

    @Override
    public FullEmployeeCycleDto getEmployeeCycleById(UUID employeeCycleId) {
        var employeeCycle = getById(employeeCycleId);
        var fullEmployeeCycle = employeeCycleMapper.employeeCycleToFullEMployeeCycleDto(employeeCycle);
        var timelines = timelineDao.getByEmployeeCycleId(employeeCycleId);
        fullEmployeeCycle.setTimelines(timelines);
        return fullEmployeeCycle;
    }


    private EmployeeCycle getById(UUID employeeCycleId) {
        var employeeCycle = employeeCycleDao.getById(employeeCycleId);
        if (employeeCycle == null) {
            throw new NotFoundException("", "Employee Cycle not found with id " + employeeCycleId);
        }
        return employeeCycle;
    }
}