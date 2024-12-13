package com.learning.emsmybatisliquibase.scheduled;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dao.PeriodDao;
import com.learning.emsmybatisliquibase.dao.EmployeePeriodDao;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import com.learning.emsmybatisliquibase.entity.PeriodStatus;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.service.PeriodService;
import com.learning.emsmybatisliquibase.service.EmployeePeriodService;
import com.learning.emsmybatisliquibase.service.NotificationService;
import com.learning.emsmybatisliquibase.service.ReviewTimelineService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduledTasks {

    private final EmployeeDao employeeDao;

    private final ProfileDao profileDao;

    private final PeriodDao periodDao;

    private final ReviewTimelineService reviewTimelineService;

    private final EmployeePeriodDao employeePeriodDao;

    private final EmployeePeriodService employeePeriodService;

    private final PeriodService periodService;

    private final NotificationService notificationService;

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

                var employeeCycles = employeePeriodDao.getByEmployeeIdAndStatus(employee.getUuid(), PeriodStatus.STARTED);
                employeeCycles.forEach(employeeCycle -> employeePeriodService.updateEmployeePeriodStatus(employeeCycle.getUuid(), PeriodStatus.INACTIVE));
            }
        }
    }

    @Scheduled(cron = "0 0 0 25 12 *")
    public void schedulePeriod() {
        var year = Instant.now().atZone(ZoneId.systemDefault()).getYear() + 1;
        periodService.createPeriod(year);
    }

    @Scheduled(cron = "0 0 0 1 1 *")
    public void startPeriod() {
        var oldPeriod = periodDao.getByStatus(PeriodStatus.STARTED);
        oldPeriod.setStatus(PeriodStatus.INACTIVE);
        oldPeriod.setUpdatedTime(Instant.now());

        var employeePeriods = employeePeriodDao.getByStatusAndPeriodId(PeriodStatus.STARTED,
                oldPeriod.getUuid());
        employeePeriods.forEach(employeePeriod ->
                employeePeriodService.updateEmployeePeriodStatus(employeePeriod.getUuid(),
                        PeriodStatus.COMPLETED));

        var period = periodDao.getByStatus(PeriodStatus.SCHEDULED);

        period.setStatus(PeriodStatus.STARTED);
        period.setUpdatedTime(Instant.now());
        try {
            if (0 == periodDao.update(period)) {
                throw new IntegrityException("PERIOD_NOT_UPDATED", "Period not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException("PERIOD_NOT_UPDATED", exception.getCause().getMessage());
        }

        employeePeriodService.periodAssignment(employeeDao.getAllActiveEmployeeIds());
    }

    @Scheduled(cron = "0 15 0 1 4,7,10 *")
    public void startTimeline() {
        var calender = Calendar.getInstance();
        var month = calender.get(Calendar.MONTH);

        ReviewType completedReviewType = null;
        ReviewType startedReviewType = null;

        switch (month) {
            case Calendar.APRIL:
                completedReviewType = ReviewType.Q1;
                startedReviewType = ReviewType.Q2;
                break;
            case Calendar.JULY:
                completedReviewType = ReviewType.Q2;
                startedReviewType = ReviewType.Q3;
                break;
            case Calendar.OCTOBER:
                completedReviewType = ReviewType.Q3;
                startedReviewType = ReviewType.Q4;
                break;
            default:
                break;
        }
        if (completedReviewType != null) {
            reviewTimelineService.startTimelinesForQuarter(completedReviewType, startedReviewType);
            notificationService.sendStartNotification(startedReviewType);
        }
    }

    @Scheduled(cron = "0 0 0 25 3,6,9,12 *")
    public void sendBeforeStartNotification() {
        var calender = Calendar.getInstance();
        var month = calender.get(Calendar.MONTH);

        ReviewType reviewType = null;
        switch (month) {
            case Calendar.MARCH:
                reviewType = ReviewType.Q1;
                break;
            case Calendar.JUNE:
                reviewType = ReviewType.Q2;
                break;
            case Calendar.SEPTEMBER:
                reviewType = ReviewType.Q3;
                break;
            case Calendar.DECEMBER:
                reviewType = ReviewType.Q4;
                break;
            default:
                break;
        }

        if (reviewType != null) {
            notificationService.sendNotificationBeforeStart(reviewType);
        }
    }
}