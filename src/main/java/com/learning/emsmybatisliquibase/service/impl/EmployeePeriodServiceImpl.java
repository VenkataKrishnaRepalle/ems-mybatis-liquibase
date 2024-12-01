package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PeriodDao;
import com.learning.emsmybatisliquibase.dao.EmployeePeriodDao;
import com.learning.emsmybatisliquibase.dao.ReviewTimelineDao;
import com.learning.emsmybatisliquibase.dto.FullEmployeePeriodDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeePeriodMapper;
import com.learning.emsmybatisliquibase.service.EmployeePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_NOT_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeePeriodErrorCodes.EMPLOYEE_PERIOD_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeePeriodErrorCodes.EMPLOYEE_PERIOD_NOT_UPDATED;
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

    private final EmployeePeriodMapper employeePeriodMapper;


    @Override
    @Transactional
    public SuccessResponseDto periodAssignment(List<UUID> employeeIds) {
        var period = getCurrentActivePeriod();

        for (var employeeId : employeeIds) {
            handleEmployeePeriodAssignment(employeeId, period);
        }

        return successResponse();
    }

    private Period getCurrentActivePeriod() {
        var period = periodDao.getByStatus(PeriodStatus.STARTED);

        if (period == null) {
            throw new NotFoundException(PERIOD_NOT_EXISTS.code(), "No Active Period for Current Period");
        }
        return period;
    }

    private void handleEmployeePeriodAssignment(UUID employeeId, Period period) {
        if (!employeePeriodDao.getByEmployeeIdAndPeriodId(employeeId, period.getUuid()).isEmpty()) {
            return;
        }

        var employeePeriods = employeePeriodDao.getByEmployeeId(employeeId);
        employeePeriods.stream()
                .filter(employeePeriod -> employeePeriod.getStatus().equals(PeriodStatus.STARTED))
                .forEach(employeePeriod -> {
                    if (employeePeriod.getPeriodUuid().equals(period.getUuid())) {
                        updateEmployeePeriodStatus(employeePeriod.getUuid(), PeriodStatus.INACTIVE);
                    } else if (!period.getStatus().equals(PeriodStatus.SCHEDULED)) {
                        updateEmployeePeriodStatus(employeePeriod.getUuid(), PeriodStatus.COMPLETED);
                    }
                });

        var employeePeriod = saveEmployeePeriod(employeeId, period);

        var currentMonth = LocalDateTime.now().getMonth();
        var year = period.getStartTime().atZone(ZoneId.systemDefault()).getYear();
        var timelines = generateTimelines(employeePeriod, currentMonth, year);
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

    private EmployeePeriod saveEmployeePeriod(UUID employeeId, Period period) {
        var employeePeriod = EmployeePeriod.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employeeId)
                .periodUuid(period.getUuid())
                .status(period.getStatus())
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        try {
            if (0 == employeePeriodDao.insert(employeePeriod)) {
                throw new IntegrityException(EMPLOYEE_PERIOD_NOT_CREATED.code(), "Employee Period not created");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_PERIOD_NOT_CREATED.code(), exception.getCause().getMessage());
        }
        return employeePeriod;
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
                    .employeePeriodUuid(employeePeriod.getUuid())
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
    public SuccessResponseDto updateEmployeePeriodsByPeriodId(UUID periodId, PeriodStatus status) {
        var employeePeriods = employeePeriodDao.getByStatusAndPeriodId(PeriodStatus.STARTED, periodId);
        employeePeriods.forEach(employeePeriod -> updateEmployeePeriodStatus(employeePeriod.getUuid(), status));

        return successResponse();
    }


    @Override
    public SuccessResponseDto updateEmployeePeriodStatus(UUID employeePeriodId, PeriodStatus status) {
        var employeePeriod = getById(employeePeriodId);
        employeePeriod.setStatus(status);

        try {
            if (0 == employeePeriodDao.update(employeePeriod)) {
                throw new IntegrityException(EMPLOYEE_PERIOD_NOT_UPDATED.code(), "Employee Period not updated with id: " + employeePeriod.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_PERIOD_NOT_UPDATED.code(), exception.getCause().getMessage());
        }

        var timelines = reviewTimelineDao.getByEmployeePeriodId(employeePeriodId);
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
    public FullEmployeePeriodDto getEmployeePeriodById(UUID employeePeriodId) {
        var employeePeriod = getById(employeePeriodId);
        var fullEmployeePeriod = employeePeriodMapper.employeePeriodToFullEmployeePeriodDto(employeePeriod);
        var timelines = reviewTimelineDao.getByEmployeePeriodId(employeePeriodId);
        fullEmployeePeriod.setReviewTimelines(timelines);
        return fullEmployeePeriod;
    }

    @Override
    public List<EmployeePeriod> getByEmployeeIdAndPeriodId(UUID employeeId, UUID periodId) {
        var employeePeriods = employeePeriodDao.getByEmployeeIdAndPeriodId(employeeId, periodId);

        var latestEmployeePeriod = employeePeriods.stream()
                .filter(employeePeriod -> employeePeriod.getUpdatedTime() != null)
                .max(Comparator.comparing(EmployeePeriod::getUpdatedTime));

        return latestEmployeePeriod.map(List::of).orElseGet(List::of);
    }


    private EmployeePeriod getById(UUID employeePeriodId) {
        var employeePeriod = employeePeriodDao.getById(employeePeriodId);
        if (employeePeriod == null) {
            throw new NotFoundException("", "Employee Period not found with id " + employeePeriodId);
        }
        return employeePeriod;
    }

    private SuccessResponseDto successResponse() {
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(UUID.randomUUID()))
                .build();
    }
}