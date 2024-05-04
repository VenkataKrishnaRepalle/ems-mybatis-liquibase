package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.AttendanceDao;
import com.learning.emsmybatisliquibase.dto.ApplyAttendanceDto;
import com.learning.emsmybatisliquibase.dto.UpdateAttendanceDto;
import com.learning.emsmybatisliquibase.dto.ViewEmployeeAttendanceDto;
import com.learning.emsmybatisliquibase.entity.Attendance;
import com.learning.emsmybatisliquibase.entity.AttendanceStatus;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.mapper.AttendanceMapper;
import com.learning.emsmybatisliquibase.service.AttendanceService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.UUID;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


import static com.learning.emsmybatisliquibase.exception.errorcodes.AttendanceErrorCodes.ATTENDANCE_NOT_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.AttendanceErrorCodes.ATTENDANCE_ALREADY_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.AttendanceErrorCodes.ATTENDANCE_NOT_UPDATED;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceDao attendanceDao;

    private final EmployeeService employeeService;

    private final AttendanceMapper attendanceMapper;

    private static final String DATE_FORMAT = "MM-dd-yyyy";

    @Override
    public List<Attendance> apply(UUID employeeUuid, List<ApplyAttendanceDto> attendanceDtos) {
        employeeService.getById(employeeUuid);

        var appliedAttendances = attendanceDao.getByEmployeeUuid(employeeUuid);

        for (var attendanceDto : attendanceDtos) {
            var formattedDate = new SimpleDateFormat(DATE_FORMAT).format(attendanceDto.getDate());
            for (var attendance : appliedAttendances) {
                if (formattedDate.equals(new SimpleDateFormat(DATE_FORMAT).format(attendance.getDate()))) {
                    throw new FoundException(ATTENDANCE_ALREADY_EXISTS.code(), "Attendance already applied for date " + formattedDate);
                }
            }
        }

        var attendances = attendanceDtos.stream()
                .map(attendanceMapper::applyAttendanceDtoToAttendance)
                .toList();
        attendances.forEach(attendance -> {
            attendance.setUuid(UUID.randomUUID());
            attendance.setEmployeeUuid(employeeUuid);
            attendance.setStatus(AttendanceStatus.SUBMITTED);
            attendance.setCreatedTime(Instant.now());
            attendance.setUpdatedTime(Instant.now());
        });
        attendances.forEach(attendanceDao::insert);
        return attendances;
    }

    @Override
    public Attendance update(UUID employeeUuid, UUID attendanceUuid, UpdateAttendanceDto attendanceDto) {
        var attendance = getByUuid(employeeUuid, attendanceUuid);

        attendance.setWorkMode(attendanceDto.getWorkMode());
        attendance.setType(attendanceDto.getType());
        attendance.setStatus(attendanceDto.getStatus());

        if (0 == attendanceDao.update(attendance)) {
            throw new InvalidInputException(ATTENDANCE_NOT_UPDATED.code(), "Attendance Failed to Update");
        }
        return attendance;
    }

    @Override
    public Attendance getByUuid(UUID employeeUuid, UUID attendanceUuid) {
        employeeService.getById(employeeUuid);
        var attendance = attendanceDao.getById(attendanceUuid);
        if (!attendance.getEmployeeUuid().equals(employeeUuid)) {
            throw new InvalidInputException(ATTENDANCE_NOT_EXISTS.code(), "Attendance not exists for employeeId: " + employeeUuid + " and attendanceId: " + attendanceUuid);
        }
        return attendance;
    }

    @Override
    public ViewEmployeeAttendanceDto getEmployeeAttendance(UUID employeeUuid) {
        var employee = employeeService.getById(employeeUuid);

        var attendances = attendanceDao.getByEmployeeUuid(employeeUuid);

        Map<AttendanceStatus, List<Attendance>> attendanceStatusListMap = new EnumMap<>(AttendanceStatus.class);

        for (AttendanceStatus status : AttendanceStatus.values()) {
            attendanceStatusListMap.put(status, filterAttendanceByStatus(attendances, status));
        }

        return ViewEmployeeAttendanceDto.builder()
                .employeeUuid(employee.getUuid())
                .employeeFirstName(employee.getFirstName())
                .employeeLastName(employee.getLastName())
                .submittedAttendance(attendanceStatusListMap.get(AttendanceStatus.SUBMITTED))
                .waitingForCancellationAttendance(attendanceStatusListMap.get(AttendanceStatus.WAITING_FOR_CANCELLATION))
                .cancelledAttendance(attendanceStatusListMap.get(AttendanceStatus.CANCELLED))
                .build();
    }

    @Override
    public List<ViewEmployeeAttendanceDto> getAllEmployeesAttendanceByManager(UUID managerUuid) {
        employeeService.isManager(managerUuid);

        return employeeService.getByManagerUuid(managerUuid)
                .stream()
                .map(employee -> getEmployeeAttendance(employee.getUuid()))
                .toList();
    }

    @Override
    public List<ViewEmployeeAttendanceDto> getFullTeamAttendance(UUID employeeUuid) {
        var fullTeam = employeeService.getFullTeam(employeeUuid);
        List<ViewEmployeeAttendanceDto> employeeAttendance = new ArrayList<>();
        for (var employee : fullTeam) {
            employeeAttendance.add(getEmployeeAttendance(employee.getUuid()));
        }
        return employeeAttendance;
    }

    private List<Attendance> filterAttendanceByStatus(List<Attendance> attendances, AttendanceStatus status) {
        return attendances.stream()
                .filter(attendance -> attendance.getStatus().equals(status))
                .toList();
    }
}
