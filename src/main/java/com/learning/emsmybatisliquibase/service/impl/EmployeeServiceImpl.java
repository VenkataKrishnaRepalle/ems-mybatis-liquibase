package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.*;
import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeMapper;
import com.learning.emsmybatisliquibase.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.*;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;

    private final PasswordService passwordService;

    private final EmployeeMapper employeeMapper;

    private final DepartmentService departmentService;

    private final ProfileService profileService;

    private final EmployeePeriodService employeePeriodService;

    private final PeriodService periodService;

    private final EmployeePeriodDao employeePeriodDao;

    private final NotificationService notificationService;

    private final Random random = new Random();


    @Override
    @Transactional
    public AddEmployeeResponseDto add(AddEmployeeDto employeeDto) throws MessagingException,
            UnsupportedEncodingException {
        if (employeeDto.getManagerUuid() != null) {
            isManager(employeeDto.getManagerUuid());
        }

        var employeeByEmail = employeeDao.getByEmail(employeeDto.getEmail());
        if (employeeByEmail != null) {
            throw new FoundException(EMPLOYEE_ALREADY_EXISTS.code(), "Employee with given email already exists");
        }

        boolean isManager = "T".equalsIgnoreCase(employeeDto.getIsManager());
        var employee = employeeMapper.addEmployeeDtoToEmployee(employeeDto);
        employee.setUuid(UUID.randomUUID());
        employee.setIsManager(isManager);
        employee.setManagerUuid(employeeDto.getManagerUuid());
        employee.setCreatedTime(Instant.now());
        employee.setUpdatedTime(Instant.now());

        try {
            if (0 == employeeDao.insert(employee)) {
                throw new NotFoundException(EMPLOYEE_NOT_CREATED.code(), "Failed in saving employee");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_NOT_CREATED.code(), exception.getCause().getMessage());
        }

        String password;
        if (Boolean.TRUE.equals(validatePasswords(employeeDto.getPassword(), employeeDto.getConfirmPassword()))) {
            password = employeeDto.getPassword();
        } else {
            password = generateRandomPassword();
        }

        passwordService.create(employee.getUuid(),
                PasswordDto.builder()
                        .password(password)
                        .confirmPassword(password)
                        .build());

        Department department = null;
        if (employeeDto.getDepartmentName() != null) {
            department = departmentService.add(new AddDepartmentDto(employeeDto.getDepartmentName().trim()));
        }

        var profile = Profile.builder()
                .profileStatus(profileStatus(employeeDto))
                .jobTitle(JobTitleType.valueOf(employeeDto.getJobTitle()))
                .employeeUuid(employee.getUuid())
                .departmentUuid(department == null ? null : department.getUuid())
                .updatedTime(Instant.now())
                .build();

        profileService.insert(profile);

        employeePeriodService.periodAssignment(List.of(employee.getUuid()));

        if (Boolean.FALSE.equals(validatePasswords(employeeDto.getPassword(), employeeDto.getConfirmPassword()))) {
            notificationService.sendSuccessfulEmployeeOnBoard(employee, password, 0);
        } else {
            notificationService.sendSuccessfulEmployeeOnBoard(employee, password, 1);
        }


        var response = employeeMapper.employeeToAddEmployeeResponseDto(employee);
        response.setProfile(profile);
        response.setDepartment(department);
        response.setIsManager(isManager);

        return response;
    }


    private ProfileStatus profileStatus(AddEmployeeDto employeeDto) {
        ProfileStatus profileStatus;
        Boolean value = validatePasswords(employeeDto.getPassword(), employeeDto.getConfirmPassword());
        if (employeeDto.getLeavingDate() != null && employeeDto.getLeavingDate().isAfter(LocalDate.now())) {
            profileStatus = ProfileStatus.INACTIVE;
        } else if ((employeeDto.getLeavingDate() == null ||
                employeeDto.getLeavingDate().isBefore(LocalDate.now())) && Boolean.TRUE.equals(!value)) {
            profileStatus = ProfileStatus.PENDING;
        } else if ((employeeDto.getLeavingDate() == null ||
                employeeDto.getLeavingDate().isBefore(LocalDate.now())) && Boolean.TRUE.equals(value)) {
            profileStatus = ProfileStatus.ACTIVE;
        } else {
            profileStatus = ProfileStatus.PENDING;
        }
        return profileStatus;
    }

    private Boolean validatePasswords(String password, String confirmPassword) {
        return StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(confirmPassword);
    }

    @Override
    public Employee getById(UUID id) {
        var employee = employeeDao.get(id);
        if (employee == null) {
            throw new NotFoundException(EMPLOYEE_NOT_FOUND.code(), "employee not found with id: " + id);
        }
        return employee;
    }


    @Override
    public void updateLeavingDate(UUID id, UpdateLeavingDateDto updateLeavingDate) {
        var employee = getById(id);
        try {
            if (0 == employeeDao.updateLeavingDate(updateLeavingDate.getLeavingDate(), id)) {
                throw new InvalidInputException(EMPLOYEE_INTEGRATE_VIOLATION.code(),
                        "Error in updating LeavingDate");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_INTEGRATE_VIOLATION.code(),
                    exception.getCause().getMessage());
        }

        var profile = profileService.getByEmployeeUuid(id);

        if (updateLeavingDate.getLeavingDate() == null && profile.getProfileStatus().equals(ProfileStatus.INACTIVE)) {
            var currentActiveCycle = periodService.getCurrentActivePeriod();
            var employeeCycle = employeePeriodDao.getByEmployeeIdAndPeriodId(employee.getUuid(),
                    currentActiveCycle.getUuid());
            if (employeeCycle != null) {
                employeePeriodService.updateEmployeePeriodStatus(employeeCycle.getUuid(),
                        PeriodStatus.STARTED);
            }
            profile.setProfileStatus(ProfileStatus.ACTIVE);
        } else if (updateLeavingDate.getLeavingDate() != null &&
                updateLeavingDate.getLeavingDate().before(new Date())) {
            profile.setProfileStatus(ProfileStatus.INACTIVE);
            var empStartedCycles = employeePeriodDao.getByEmployeeIdAndStatus(employee.getUuid(),
                    PeriodStatus.STARTED);
            empStartedCycles.forEach(employeeCycle ->
                    employeePeriodService.updateEmployeePeriodStatus(employeeCycle.getUuid(),
                            PeriodStatus.INACTIVE));
        }
        profileService.update(profile);
    }


    @Override
    public List<Employee> getAll() {
        return employeeDao.getAll();
    }


    public void update(Employee employee) {
        try {
            if (0 == employeeDao.update(employee)) {
                throw new NotFoundException(EMPLOYEE_NOT_UPDATED.code(), "Failed in updating employee manager status");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(EMPLOYEE_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
    }

    public Employee getByEmail(String email) {
        var employee = employeeDao.getByEmail(email.trim());
        if (employee == null) {
            throw new NotFoundException(EMPLOYEE_NOT_FOUND.code(), "Employee not found with email " + email);
        }
        return employee;
    }

    @Override
    public List<Employee> getByManagerUuid(UUID managerId) {
        var employee = getById(managerId);
        if (employee.getIsManager().equals(Boolean.TRUE)) {
            return getAllByManagerUuid(managerId);
        } else {
            throw new NotFoundException(MANAGER_ACCESS_NOT_FOUND.code(), "User don't have manager access");
        }
    }

    private List<Employee> getAllByManagerUuid(UUID mangerUuid) {
        return employeeDao.getAllByManagerUuid(mangerUuid);
    }

    public void isManager(UUID uuid) {
        var manager = getById(uuid);
        if (manager.getIsManager().equals(Boolean.FALSE)) {
            throw new NotFoundException(MANAGER_ACCESS_NOT_FOUND.code(), "User is not a Manager");
        }
    }

    @Override
    public List<EmployeeAndManagerDto> getFullTeam(UUID employeeId) {
        getById(employeeId);
        isManager(employeeId);

        List<EmployeeAndManagerDto> employees = new ArrayList<>();

        for (var employee : getAllByManagerUuid(employeeId)) {
            var employee1 = employeeMapper.employeeToEmployeeAndManagerDto(employee);
            employee1.setManagerUuid(getById(employee1.getManagerUuid()).getUuid());
            if (employee.getIsManager().equals(Boolean.TRUE)) {
                employees.add(employee1);
                employees.addAll(getFullTeam(employee1.getUuid()));
            } else {
                employees.add(employee1);
            }
        }
        return employees;
    }

    @Override
    public EmployeeFullReportingChainDto getEmployeeFullReportingChain(UUID employeeId) {
        var employee = getById(employeeId);
        var response = employeeMapper.employeeResponseDtoToEmployeeFullReportingChainDto(employee);
        if (employee.getManagerUuid() != null) {
            response.setManager(getEmployeeFullReportingChain(employee.getManagerUuid()));
        }
        return response;
    }

    @Override
    public EmployeeResponseDto getMe() {
        var employeeUuid = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());
        return employeeDao.getMe(employeeUuid);
    }

    @Override
    public PaginatedResponse<Employee> getAllByPagination(int page, int size, String sortBy, String sortOrder) {
        int offSet = (page - 1) * size;
        var employees = employeeDao.findAll(size, offSet, sortBy, sortOrder);
        var totalItems = employeeDao.employeesCount(ProfileStatus.ACTIVE);
        return PaginatedResponse.<Employee>builder()
                .data(employees)
                .totalItems(totalItems)
                .totalPages((int) Math.ceil((double) totalItems / size))
                .currentPage(page)
                .build();
    }

    @Override
    public List<EmployeeDetailsDto> getAllActiveManagers() {
        return employeeDao.getAllActiveManagers();
    }

    private String generateRandomPassword() {
        var characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var sb = new StringBuilder(10);

        for (int i = 0; i < sb.capacity(); i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
