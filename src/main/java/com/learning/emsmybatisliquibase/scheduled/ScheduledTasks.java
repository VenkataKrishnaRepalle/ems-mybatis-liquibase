package com.learning.emsmybatisliquibase.scheduled;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dao.CycleDao;
import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.Timeline;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.service.CycleService;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduledTasks {

    private final EmployeeDao employeeDao;

    private final ProfileDao profileDao;

    private final CycleDao cycleDao;

    private final TimelineDao timelineDao;

    private final EmployeeCycleDao employeeCycleDao;

    private final EmployeeCycleService employeeCycleService;

    private final CycleService cycleService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateProfileStatusLeavingDate() {
        List<Employee> employees = employeeDao.getActiveEmployeesWithPastLeavingDate();
        if (employees.isEmpty()) {
            return;
        }
        for (Employee employee : employees) {
            var profile = profileDao.get(employee.getUuid());
            if (employee.getLeavingDate() != null && profile.getProfileStatus() != ProfileStatus.INACTIVE) {
                profile.setProfileStatus(ProfileStatus.INACTIVE);
                profile.setUpdatedTime(Instant.now());
                if (0 == profileDao.update(profile)) {
                    throw new IntegrityException("", "");
                }

                var employeeCycles = employeeCycleDao.getByEmployeeIdAndStatus(employee.getUuid(), CycleStatus.STARTED);
                employeeCycles.forEach(employeeCycle -> employeeCycleService.updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.INACTIVE));
            }
        }
    }

    @Scheduled(cron = "0 0 0 25 12 *")
    public void scheduleCycle() {
        var year = Instant.now().atZone(ZoneId.systemDefault()).getYear() + 1;
        cycleService.createCycle(year);
    }

    @Scheduled(cron = "0 0 0 1 1 *")
    public void startCycle() {
        var oldCycle = cycleDao.getByStatus(CycleStatus.STARTED);
        oldCycle.setStatus(CycleStatus.INACTIVE);
        oldCycle.setUpdatedTime(Instant.now());

        var employeeCycles = employeeCycleDao.getByStatusAndCycleId(CycleStatus.STARTED, oldCycle.getUuid());
        employeeCycles.forEach(employeeCycle -> employeeCycleService.updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.COMPLETED));

        var cycle = cycleDao.getByStatus(CycleStatus.SCHEDULED);

        cycle.setStatus(CycleStatus.STARTED);
        cycle.setUpdatedTime(Instant.now());
        try {
            if (0 == cycleDao.update(cycle)) {
                throw new IntegrityException("CYCLE_NOT_UPDATED", "Cycle not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException("CYCLE_NOT_UPDATED", exception.getCause().getMessage());
        }

        employeeCycleService.cycleAssignment(employeeDao.getAllActiveEmployeeIds());
    }

    @Scheduled(cron = "0 15 0 1 4,7,10 *")
    public void startTimeline() {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int month = calendar.get(Calendar.MONTH) + 1;

        switch (month) {
            case 4:
                startTimelinesForQuarter(ReviewType.Q1, ReviewType.Q2);
                break;
            case 7:
                startTimelinesForQuarter(ReviewType.Q2, ReviewType.Q3);
                break;
            case 10:
                startTimelinesForQuarter(ReviewType.Q3, ReviewType.Q4);
                break;
            default:
                break;
        }
    }

    private void startTimelinesForQuarter(ReviewType completedReviewType, ReviewType startedReviewType) {
        List<Timeline> completedTimelines = timelineDao.findByStatusAndReviewType(CycleStatus.STARTED, completedReviewType);
        completedTimelines.forEach(timeline -> {
            timeline.setStatus(TimelineStatus.COMPLETED);
            timeline.setUpdatedTime(Instant.now());
            if (0 == timelineDao.update(timeline)) {
                throw new IntegrityException("", "");
            }
        });

        List<Timeline> startedTimelines = timelineDao.findByStatusAndReviewType(CycleStatus.SCHEDULED, startedReviewType);
        startedTimelines.forEach(timeline -> {
            timeline.setStatus(TimelineStatus.STARTED);
            timeline.setUpdatedTime(Instant.now());
            if (0 == timelineDao.update(timeline)) {
                throw new IntegrityException("", "");
            }
        });
    }
}