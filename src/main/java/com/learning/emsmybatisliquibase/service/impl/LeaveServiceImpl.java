package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dto.ApplyLeaveDto;
import com.learning.emsmybatisliquibase.dao.LeaveDao;
import com.learning.emsmybatisliquibase.dto.ParseLeaveRequestDto;
import com.learning.emsmybatisliquibase.dto.ParseLeaveResponseDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.dto.UpdateLeaveByManagerDto;
import com.learning.emsmybatisliquibase.dto.ViewEmployeeLeavesDto;
import com.learning.emsmybatisliquibase.entity.Leave;
import com.learning.emsmybatisliquibase.entity.LeaveStatus;
import com.learning.emsmybatisliquibase.entity.LeaveType;
import com.learning.emsmybatisliquibase.exception.AlreadyFoundException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.LeaveMapper;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveDao leaveDao;

    private final EmployeeService employeeService;

    private final LeaveMapper leaveMapper;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public List<ParseLeaveResponseDto> parseLeave(ParseLeaveRequestDto parseLeaveDto) {
        var dates = new ArrayList<>();

        var calender = Calendar.getInstance();
        calender.setTime(parseLeaveDto.getStartDate());

        while (!calender.getTime().after(parseLeaveDto.getEndDate())) {
            dates.add(calender.getTime());
            calender.add(Calendar.DATE, 1);
        }

        List<ParseLeaveResponseDto> response = new ArrayList<>();
        for (var date : dates) {
            response.add(ParseLeaveResponseDto.builder()
                    .date((Date) date)
                    .type(LeaveType.FULL_DAY)
                    .build());
        }
        return response;
    }

    @Override
    public List<Leave> applyLeave(UUID employeeId, List<ApplyLeaveDto> applyLeaveDto) {
        employeeService.getById(employeeId);
        var appliedLeaves = leaveDao.getLeavesByEmployee(employeeId);

        applyLeaveDto.stream()
                .map(ApplyLeaveDto::getDate)
                .map(date -> new SimpleDateFormat(DATE_FORMAT).format(date))
                .forEach(formattedDate -> {
                    if (appliedLeaves.stream()
                            .map(Leave::getDate)
                            .map(Object::toString)
                            .anyMatch(formattedDate::equals)) {
                        throw new AlreadyFoundException("Leave already applied for date " + formattedDate);
                    }
                });

        var leaves = leaveMapper.applyLeaveDtoToLeaveDay(applyLeaveDto);

        leaves.forEach(leave -> {
            leave.setUuid(UUID.randomUUID());
            leave.setEmployeeUuid(employeeId);
            leave.setStatus(LeaveStatus.WAITING_FOR_APPROVAL);
            leave.setCreatedTime(Instant.now());
            leave.setUpdatedTime(Instant.now());
        });

        leaves.forEach(leave -> {
            if (0 == leaveDao.insert(leave)) {
                throw new InvalidInputException("Leave not saved");
            }
        });

        return leaves;
    }

    @Override
    public ViewEmployeeLeavesDto getLeavesByEmployeeId(UUID employeeId) {

        var employee = employeeService.getById(employeeId);
        var leaves = leaveDao.getLeavesByEmployee(employeeId);

        Map<LeaveStatus, List<Leave>> leaveStatusMap = new EnumMap<>(LeaveStatus.class);
        Map<Boolean, List<Leave>> dateLeavesmap = leaves.stream()
                .collect(Collectors.partitioningBy(leave -> leave.getDate().compareTo(new Date()) > 0));

        for (LeaveStatus status : LeaveStatus.values()) {
            leaveStatusMap.put(status, filterLeavesByStatus(leaves, status));
        }

        return ViewEmployeeLeavesDto.builder()
                .employeeUuid(employeeId)
                .employeeFirstName(employee.getFirstName())
                .employeeLastName(employee.getLastName())
                .pastLeaves(dateLeavesmap.get(Boolean.FALSE))
                .futureLeaves(dateLeavesmap.get(Boolean.TRUE))
                .approvedLeaves(leaveStatusMap.get(LeaveStatus.APPROVED))
                .waitingForApprovalLeaves(leaveStatusMap.get(LeaveStatus.WAITING_FOR_APPROVAL))
                .declinedLeaves(leaveStatusMap.get(LeaveStatus.DECLINED))
                .cancelledLeaves(leaveStatusMap.get(LeaveStatus.CANCELLED))
                .build();
    }

    @Override
    public List<ViewEmployeeLeavesDto> getAllEmployeesLeavesByManager(UUID managerUuid) {
        employeeService.isManager(managerUuid);

        return employeeService.getByManagerUuid(managerUuid)
                .stream()
                .map(employee -> getLeavesByEmployeeId(employee.getUuid()))
                .toList();
    }

    @Override
    public SuccessResponseDto updateLeavesByManager(UUID managerUuid, LeaveStatus status, List<UpdateLeaveByManagerDto> leavesDto) {
        employeeService.isManager(managerUuid);

        var leaves = leavesDto.stream()
                .filter(leaveDto -> isLeaveEligibleForManager(leaveDto.getLeaveUuid(), managerUuid))
                .map(leaveDto -> {
                    var leave = getLeaveById(leaveDto.getLeaveUuid());
                    leave.setStatus(status);
                    leave.setComments(leaveDto.getComments());
                    return leave;
                })
                .toList();
        leaves.forEach(leaveDao::update);
        return SuccessResponseDto.builder()
                .success(true)
                .data(status.toString())
                .build();
    }

    private List<Leave> filterLeavesByStatus(List<Leave> leaves, LeaveStatus status) {
        return leaves.stream()
                .filter(leave -> leave.getStatus().equals(status))
                .toList();
    }

    private Leave getLeaveById(UUID leaveUuid) {
        var leave = leaveDao.getById(leaveUuid);
        if (leave == null) {
            throw new NotFoundException("leave not found with id : " + leaveUuid);
        }
        return leave;
    }

    private Boolean isLeaveEligibleForManager(UUID leaveUuid, UUID managerUuid) {
        var leave = getLeaveById(leaveUuid);
        var employee = employeeService.getById(leave.getEmployeeUuid());
        if (employee.getManagerUuid() == null) {
            throw new NotFoundException("Manager Not found for this colleague to approve leave");
        } else if (Boolean.FALSE.equals(employee.getManagerUuid().equals(managerUuid))) {
            throw new NotFoundException("Invalid manager trying to approve the request");
        }
        return Boolean.TRUE;
    }
}
