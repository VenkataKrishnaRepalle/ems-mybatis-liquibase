package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PeriodDao;
import com.learning.emsmybatisliquibase.dao.EmployeePeriodDao;
import com.learning.emsmybatisliquibase.dao.ReviewTimelineDao;
import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeCycleMapper;
import com.learning.emsmybatisliquibase.service.EmployeePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_NOT_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeePeriodErrorCodes.EMPLOYEE_CYCLE_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeePeriodErrorCodes.EMPLOYEE_CYCLE_NOT_UPDATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.TimelineErrorCodes.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeePeriodServiceImpl implements EmployeePeriodService {

    private final EmployeePeriodDao employeePeriodDao;

    private final PeriodDao periodDao;

    private final ReviewTimelineDao reviewTimelineDao;

    private final EmployeeCycleMapper employeeCycleMapper;


    @Override
    @Transactional
    public SuccessResponseDto cycleAssignment(List<UUID> employeeIds) {
        var cycle = getCurrentActiveCycle();

        for (var employeeId : employeeIds) {
            handleEmployeeCycleAssignment(employeeId, cycle);
        }

        return successResponse();
    }

    private Period getCurrentActiveCycle() {
        var cycle = periodDao.getByStatus(PeriodStatus.STARTED);

        if (cycle == null) {
            throw new NotFoundException(PERIOD_NOT_EXISTS.code(), "No Active Cycle for Current Cycle");
        }
        return cycle;
    }

    private void handleEmployeeCycleAssignment(UUID employeeId, Period period) {
        if (!employeePeriodDao.getByEmployeeIdAndCycleId(employeeId, period.getUuid()).isEmpty()) {
            return;
        }

        var employeeCycles = employeePeriodDao.getByEmployeeId(employeeId);
        employeeCycles.stream()
                .filter(employeeCycle -> employeeCycle.getStatus().equals(PeriodStatus.STARTED))
                .forEach(employeeCycle -> {
                    if (employeeCycle.getCycleUuid().equals(period.getUuid())) {
                        updateEmployeeCycleStatus(employeeCycle.getUuid(), PeriodStatus.INACTIVE);
                    } else if (!period.getStatus().equals(PeriodStatus.SCHEDULED)) {
                        updateEmployeeCycleStatus(employeeCycle.getUuid(), PeriodStatus.COMPLETED);
                    }
                });

        var employeeCycle = saveEmployeeCycle(employeeId, period);

        var currentMonth = LocalDateTime.now().getMonth();
        var year = period.getStartTime().atZone(ZoneId.systemDefault()).getYear();
        var timelines = generateTimelines(employeeCycle, currentMonth, year);
        timelines.forEach(timeline -> {
            try {
                if (0 == reviewTimelineDao.insert(timeline)) {
                    throw new IntegrityException(TIMELINE_NOT_ASSIGNED.code(), "Timeline not assigned");
                }
            } catch (DataIntegrityViolationException exception) {
                throw new IntegrityException(TIMELINE_NOT_ASSIGNED.code(), exception.getCause().getMessage());
            }
        });
    }

    private EmployeePeriod saveEmployeeCycle(UUID employeeId, Period period) {
        var employeeCycle = EmployeePeriod.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employeeId)
                .cycleUuid(period.getUuid())
                .status(period.getStatus())
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        try {
            if (0 == employeePeriodDao.insert(employeeCycle)) {
                throw new IntegrityException(EMPLOYEE_CYCLE_NOT_CREATED.code(), "Employee Cycle not created");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_CYCLE_NOT_CREATED.code(), exception.getCause().getMessage());
        }
        return employeeCycle;
    }

    private List<ReviewTimeline> generateTimelines(EmployeePeriod employeePeriod, Month currentMonth, int year) {
        var timelines = new ArrayList<ReviewTimeline>();
        for (int quarter = 1; quarter <= 4; quarter++) {
            var status = ReviewTimelineStatus.SCHEDULED;

            if (quarter == currentMonth.getValue() / 3 + 1) {
                status = ReviewTimelineStatus.STARTED;
            } else if (quarter < currentMonth.getValue() / 3 + 1) {
                status = ReviewTimelineStatus.LOCKED;
            }

            var startTime = LocalDateTime.of(year, quarter * 3 - 2, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var overdueTime = LocalDateTime.of(year, quarter * 3 - 1, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var lockTime = LocalDateTime.of(year, quarter * 3, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
            var endDayOfMonth = YearMonth.of(year, quarter * 3).lengthOfMonth();
            var endTime = LocalDateTime.of(year, quarter * 3, endDayOfMonth, 23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

            var timeline = ReviewTimeline.builder()
                    .uuid(UUID.randomUUID())
                    .employeeCycleUuid(employeePeriod.getUuid())
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
    public SuccessResponseDto updateEmployeeCyclesByCycleId(UUID cycleId, PeriodStatus status) {
        var employeeCycles = employeePeriodDao.getByStatusAndCycleId(PeriodStatus.STARTED, cycleId);
        employeeCycles.forEach(employeeCycle -> updateEmployeeCycleStatus(employeeCycle.getUuid(), status));

        return successResponse();
    }


    @Override
    public SuccessResponseDto updateEmployeeCycleStatus(UUID employeeCycleId, PeriodStatus status) {
        var employeeCycle = getById(employeeCycleId);
        employeeCycle.setStatus(status);

        try {
            if (0 == employeePeriodDao.update(employeeCycle)) {
                throw new IntegrityException(EMPLOYEE_CYCLE_NOT_UPDATED.code(), "Employee Cycle not updated with id: " + employeeCycle.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_CYCLE_NOT_UPDATED.code(), exception.getCause().getMessage());
        }

        var timelines = reviewTimelineDao.getByEmployeeCycleId(employeeCycleId);
        ReviewTimelineStatus reviewTimelineStatus;
        switch (status) {
            case STARTED -> reviewTimelineStatus = ReviewTimelineStatus.STARTED;
            case COMPLETED -> reviewTimelineStatus = ReviewTimelineStatus.COMPLETED;
            case INACTIVE -> reviewTimelineStatus = ReviewTimelineStatus.LOCKED;
            default -> reviewTimelineStatus = ReviewTimelineStatus.NOT_STARTED;
        }
        timelines.forEach(timeline -> {
            timeline.setStatus(reviewTimelineStatus);
            timeline.setUpdatedTime(Instant.now());
            try {
                if (0 == reviewTimelineDao.update(timeline)) {
                    throw new IntegrityException(TIMELINE_NOT_UPDATED.code(), "Timeline not updated for id: " + timeline.getUuid());
                }
            } catch (DataIntegrityViolationException exception) {
                throw new IntegrityException(TIMELINE_NOT_UPDATED.code(), exception.getCause().getMessage());
            }
        });
        return successResponse();
    }


    @Override
    public FullEmployeeCycleDto getEmployeeCycleById(UUID employeeCycleId) {
        var employeeCycle = getById(employeeCycleId);
        var fullEmployeeCycle = employeeCycleMapper.employeeCycleToFullEMployeeCycleDto(employeeCycle);
        var timelines = reviewTimelineDao.getByEmployeeCycleId(employeeCycleId);
        fullEmployeeCycle.setReviewTimelines(timelines);
        return fullEmployeeCycle;
    }

    @Override
    public List<EmployeePeriod> getByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId) {
        var employeeCycles = employeePeriodDao.getByEmployeeIdAndCycleId(employeeId, cycleId);

        var latestEmployeeCycle = employeeCycles.stream()
                .filter(employeeCycle -> employeeCycle.getUpdatedTime() != null)
                .max(Comparator.comparing(EmployeePeriod::getUpdatedTime));

        return latestEmployeeCycle.map(List::of).orElseGet(List::of);
    }


    private EmployeePeriod getById(UUID employeeCycleId) {
        var employeeCycle = employeePeriodDao.getById(employeeCycleId);
        if (employeeCycle == null) {
            throw new NotFoundException("", "Employee Cycle not found with id " + employeeCycleId);
        }
        return employeeCycle;
    }

    private SuccessResponseDto successResponse() {
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(UUID.randomUUID()))
                .build();
    }
}