package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PeriodDao;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Period;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.PeriodService;
import com.learning.emsmybatisliquibase.service.EmployeePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_ALREADY_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_NOT_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.PeriodErrorCodes.PERIOD_NOT_UPDATED;

@Service
@RequiredArgsConstructor
public class PeriodServiceImpl implements PeriodService {

    private final PeriodDao periodDao;

    private final EmployeePeriodService employeePeriodService;

    private static final UUID ADMIN_UUID = UUID.fromString("018fb996-a741-73ce-ac0d-79916b15ac0f");

    private static final String PERIOD_NOT_UPDATED_MESSAGE = "Period not updated for periodId: ";


    @Override
    public Period createPeriod(int year) {
        var startDateTime = Year.of(year).atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        var endDateTime = Year.of(year).atDay(1)
                .plusYears(1)
                .atStartOfDay(ZoneId.systemDefault())
                .minusSeconds(1)
                .toInstant();

        var periods = periodDao.getAll();
        for (var period : periods) {
            if (period.getStatus().equals(PeriodStatus.SCHEDULED)
                    && period.getStartTime().atZone(ZoneId.systemDefault()).getYear() == year
                    && period.getEndTime().atZone(ZoneId.systemDefault()).getYear() == year) {
                throw new FoundException(PERIOD_ALREADY_EXISTS.code(), "Period already created with scheduled Status " + period.getUuid());
            }
        }

        var period = Period.builder()
                .uuid(UUID.randomUUID())
                .name(String.valueOf(year))
                .description(year + " Period")
                .startTime(startDateTime)
                .endTime(endDateTime)
                .status(PeriodStatus.SCHEDULED)
                .createdBy(SecurityContextHolder.getContext().getAuthentication() == null ? ADMIN_UUID : UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()))
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        try {
            if (0 == periodDao.insert(period)) {
                throw new IntegrityException(PERIOD_NOT_CREATED.code(), "Period not created");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(PERIOD_NOT_CREATED.code(), "Period not created");
        }

        return period;
    }

    @Override
    public SuccessResponseDto startScheduled(UUID periodId) {
        var period = getById(periodId);
        if (!period.getStatus().equals(PeriodStatus.SCHEDULED)) {
            throw new InvalidInputException("INVALID_PERIOD_STATUS", "Provided period is not in scheduled status");
        }
        var periodStartTime = period.getStartTime().atZone(ZoneId.systemDefault()).getYear();
        var periodEndTime = period.getEndTime().atZone(ZoneId.systemDefault()).getYear();

        var activePeriod = periodDao.getByStatus(PeriodStatus.STARTED);

        if (activePeriod != null && activePeriod.getStartTime().atZone(ZoneId.systemDefault()).getYear() == periodStartTime &&
                activePeriod.getEndTime().atZone(ZoneId.systemDefault()).getYear() == periodEndTime) {
            activePeriod.setStatus(PeriodStatus.INACTIVE);
            activePeriod.setUpdatedTime(Instant.now());
            update(activePeriod);
            employeePeriodService.updateEmployeeCyclesByCycleId(activePeriod.getUuid(), PeriodStatus.INACTIVE);
        } else if (activePeriod != null && activePeriod.getStartTime().atZone(ZoneId.systemDefault()).getYear() < periodStartTime) {
            activePeriod.setStatus(PeriodStatus.COMPLETED);
            activePeriod.setUpdatedTime(Instant.now());
            update(activePeriod);
            employeePeriodService.updateEmployeeCyclesByCycleId(activePeriod.getUuid(), PeriodStatus.COMPLETED);
        }

        period.setStatus(PeriodStatus.STARTED);
        update(period);

        return successResponse();
    }

    private void update(Period period) {
        try {
            if (0 == periodDao.update(period)) {
                throw new IntegrityException(PERIOD_NOT_UPDATED.code(), PERIOD_NOT_UPDATED_MESSAGE + period.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(PERIOD_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
    }

    @Override
    public Period getById(UUID periodId) {
        var period = periodDao.getById(periodId);
        if (period == null) {
            throw new NotFoundException(PERIOD_NOT_EXISTS.code(), "Period not found with id " + periodId);
        }
        return period;
    }

    @Override
    public SuccessResponseDto updateStatus(UUID periodId, PeriodStatus status) {
        var period = getById(periodId);
        period.setStatus(status);
        period.setUpdatedTime(Instant.now());
        update(period);

        return successResponse();
    }

    @Override
    public Period getCurrentActivePeriod() {
        var period = periodDao.getByStatus(PeriodStatus.STARTED);

        if (period == null) {
            throw new NotFoundException(PERIOD_NOT_EXISTS.code(), "No Active Period for Current Period");
        }
        return period;
    }

    private SuccessResponseDto successResponse() {
        return SuccessResponseDto.builder()
                .data(UUID.randomUUID().toString())
                .success(Boolean.TRUE)
                .build();
    }
}