package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.CycleDao;
import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Cycle;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.CycleService;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.CycleErrorCodes.CYCLE_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.CycleErrorCodes.CYCLE_ALREADY_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.CycleErrorCodes.CYCLE_NOT_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.CycleErrorCodes.CYCLE_NOT_UPDATED;

@Service
@RequiredArgsConstructor
public class CycleServiceImpl implements CycleService {

    private final CycleDao cycleDao;

    private final EmployeeCycleDao employeeCycleDao;

    private final EmployeeCycleService employeeCycleService;


    @Override
    public Cycle createCycle(int year) {
        var startDateTime = Year.of(year).atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        var endDateTime = Year.of(year).atDay(1)
                .plusYears(1)
                .atStartOfDay(ZoneId.systemDefault())
                .minusSeconds(1)
                .toInstant();

        var cycles = cycleDao.getAll();
        for (var cycle : cycles) {
            if (cycle.getStatus().equals(CycleStatus.SCHEDULED)
                    && cycle.getStartTime().atZone(ZoneId.systemDefault()).getYear() == year
                    && cycle.getEndTime().atZone(ZoneId.systemDefault()).getYear() == year) {
                throw new FoundException(CYCLE_ALREADY_EXISTS.code(), "Cycle already created with scheduled Status " + cycle.getUuid());
            }
        }

        var cycle = Cycle.builder()
                .uuid(UUID.randomUUID())
                .name(String.valueOf(year))
                .description(year + " Cycle")
                .startTime(startDateTime)
                .endTime(endDateTime)
                .status(CycleStatus.SCHEDULED)
                .createdBy(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()))
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .build();

        if (0 == cycleDao.insert(cycle)) {
            throw new IntegrityException(CYCLE_NOT_CREATED.code(), "Cycle not created");
        }
        return cycle;
    }

    @Override
    public SuccessResponseDto startScheduled(UUID cycleId) {
        var cycle = getById(cycleId);
        var cycleStartTime = cycle.getStartTime().atZone(ZoneId.systemDefault()).getYear();
        var cycleEndTime = cycle.getEndTime().atZone(ZoneId.systemDefault()).getYear();

        var activeCycle = cycleDao.getByStatus(CycleStatus.STARTED);

        if (activeCycle != null && activeCycle.getStartTime().atZone(ZoneId.systemDefault()).getYear() == cycleStartTime &&
                activeCycle.getEndTime().atZone(ZoneId.systemDefault()).getYear() == cycleEndTime) {
            activeCycle.setStatus(CycleStatus.INACTIVE);
            activeCycle.setUpdatedTime(Instant.now());
            if (0 == cycleDao.update(activeCycle)) {
                throw new IntegrityException(CYCLE_NOT_UPDATED.code(), "Cycle not updated for cycleId: " + cycleId);
            }
            updateEmployeesCycleStatus(activeCycle.getUuid());
        } else if (activeCycle != null && activeCycle.getStartTime().atZone(ZoneId.systemDefault()).getYear() < cycleStartTime) {
            activeCycle.setStatus(CycleStatus.COMPLETED);
            activeCycle.setUpdatedTime(Instant.now());
            if (0 == cycleDao.update(activeCycle)) {
                throw new IntegrityException(CYCLE_NOT_UPDATED.code(), "Cycle not updated for cycleId: " + cycleId);
            }
        }

        cycle.setStatus(CycleStatus.STARTED);
        if (0 == cycleDao.update(cycle)) {
            throw new IntegrityException(CYCLE_NOT_UPDATED.code(), "Cycle not updated for cycleId: " + cycleId);
        }

        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(UUID.randomUUID()))
                .build();
    }

    @Override
    public Cycle getById(UUID cycleId) {
        var cycle = cycleDao.getById(cycleId);
        if (cycle == null) {
            throw new NotFoundException(CYCLE_NOT_EXISTS.code(), "Cycle not found with id " + cycleId);
        }
        return cycle;
    }

    @Override
    public SuccessResponseDto updateStatus(UUID cycleId, CycleStatus status) {
        var cycle = getById(cycleId);
        cycle.setStatus(status);
        cycle.setUpdatedTime(Instant.now());
        if (0 == cycleDao.update(cycle)) {
            throw new IntegrityException(CYCLE_NOT_UPDATED.code(), "Cycle not updated for cycleId: " + cycleId);
        }

        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(UUID.randomUUID()))
                .build();
    }

    @Override
    public Cycle getCurrentActiveCycle() {
        var cycle = cycleDao.getByStatus(CycleStatus.STARTED);

        if (cycle == null) {
            throw new NotFoundException(CYCLE_NOT_EXISTS.code(), "No Active Cycle for Current Cycle");
        }
        return cycle;
    }

    private void updateEmployeesCycleStatus(UUID cycleId) {
        var employeeCycles = employeeCycleDao.getByStatusAndCycleId(CycleStatus.STARTED, cycleId);
        employeeCycles.forEach(employeeCycle -> employeeCycleService.updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.INACTIVE));
    }
}