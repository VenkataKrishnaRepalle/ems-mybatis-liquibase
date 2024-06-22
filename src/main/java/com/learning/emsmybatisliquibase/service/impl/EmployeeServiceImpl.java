package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dao.EmployeeCycleDao;
import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.Gender;
import com.learning.emsmybatisliquibase.entity.JobTitleType;
import com.learning.emsmybatisliquibase.entity.Profile;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.entity.CycleStatus;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeMapper;
import com.learning.emsmybatisliquibase.service.CycleService;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.learning.emsmybatisliquibase.exception.errorcodes.DepartmentErrorCodes.DEPARTMENT_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.EMPLOYEE_NOT_FOUND;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.EMPLOYEE_ALREADY_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.EMPLOYEE_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.EMPLOYEE_INTEGRATE_VIOLATION;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.INVALID_INPUT_EXCEPTION;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.MANAGER_ACCESS_NOT_FOUND;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.INVALID_MANAGER_PROVIDED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.FileErrorCodes.INVALID_COLUMN_HEADINGS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.FileErrorCodes.SHEET_NOT_FOUND;
import static com.learning.emsmybatisliquibase.exception.errorcodes.ProfileErrorCodes.PROFILE_NOT_CREATED;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;

    private final EmployeeMapper employeeMapper;

    private final DepartmentDao departmentDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Random random = new Random();

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final ProfileDao profileDao;

    private final EmployeeCycleService employeeCycleService;

    private final CycleService cycleService;

    private final EmployeeCycleDao employeeCycleDao;

    @Value("${default.send.email}")
    String defaultEmail;

    @Value("${email.template.name.successful.onboard}")
    String emailTemplateNameSuccessfulOnboard;

    @Value("${email.template.successful.onboard}")
    String emailTemplateSuccessfulOnboard;

    private static final String ADD = "add";

    private static final String REMOVE = "remove";

    private static final String PARSE_DATE = "dd/MM/yyyy";

    @Override
    public AddEmployeeResponseDto add(AddEmployeeDto employeeDto) throws MessagingException, UnsupportedEncodingException {
        if (employeeDto.getManagerUuid() != null) {
            getById(employeeDto.getManagerUuid());
            isManager(employeeDto.getManagerUuid());
        }

        var isEmployeeExistsByEmail = employeeDao.getByEmail(employeeDto.getEmail().trim());

        if (isEmployeeExistsByEmail != null) {
            throw new FoundException(EMPLOYEE_ALREADY_EXISTS.code(), "Email already exists: " + employeeDto.getEmail());
        }
        var isManager = employeeDto.getIsManager().trim().equalsIgnoreCase("T".trim()) ? Boolean.TRUE : Boolean.FALSE;
        var employee = employeeMapper.addEmployeeDtoToEmployee(employeeDto);
        employee.setUuid(UUID.randomUUID());
        employee.setPassword(passwordEncoder.encode(generateRandomPassword()));
        employee.setIsManager(isManager);
        employee.setManagerUuid(employeeDto.getManagerUuid());
        employee.setCreatedTime(Instant.now());
        employee.setUpdatedTime(Instant.now());

        Department department = null;
        if (employeeDto.getDepartmentName() != null) {
            department = departmentDao.getByName(employeeDto.getDepartmentName().trim());
            if (department == null) {
                department = Department.builder()
                        .uuid(UUID.randomUUID())
                        .name(employeeDto.getDepartmentName())
                        .build();
                if (0 == departmentDao.insert(department)) {
                    throw new NotFoundException(DEPARTMENT_NOT_CREATED.code(), "Failed in saving department");
                }
            }
        }

        if (0 == employeeDao.insert(employee)) {
            throw new NotFoundException(EMPLOYEE_NOT_CREATED.code(), "Failed in saving employee");
        }

        sendSuccessfulEmployeeOnBoard(employee.getUuid());

        var profileStatus = employeeDto.getLeavingDate() == null || employeeDto.getLeavingDate().isAfter(LocalDate.now()) ? ProfileStatus.PENDING : ProfileStatus.INACTIVE;
        var profile = Profile.builder()
                .profileStatus(profileStatus)
                .jobTitle(JobTitleType.ENGINEER_TRAINEE)
                .employeeUuid(employee.getUuid())
                .departmentUuid(department == null ? null : department.getUuid())
                .updatedTime(Instant.now())
                .build();
        if (0 == profileDao.insert(profile)) {
            throw new NotFoundException(PROFILE_NOT_CREATED.code(), "Failed in saving profile");
        }
        List<UUID> uuid = new ArrayList<>();
        uuid.add(employee.getUuid());

        employeeCycleService.cycleAssignment(uuid);


        var response = employeeMapper.employeeToAddEmployeeResponseDto(employee);
        response.setProfile(profile);
        response.setDepartment(department);
        response.setIsManager(isManager);

        return response;
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
        if (0 == employeeDao.updateLeavingDate(updateLeavingDate.getLeavingDate(), id)) {
            throw new InvalidInputException(EMPLOYEE_INTEGRATE_VIOLATION.code(), "Problem in updating LeavingDate");
        }

        var profile = profileDao.get(id);

        if (updateLeavingDate.getLeavingDate() == null && profile.getProfileStatus().equals(ProfileStatus.INACTIVE)) {
            var currentActiveCycle = cycleService.getCurrentActiveCycle();
            var employeeCycles = employeeCycleDao.getByEmployeeIdAndCycleId(employee.getUuid(), currentActiveCycle.getUuid());
            if (employeeCycles == null) {
                return;
            } else {
                for (var employeeCycle : employeeCycles) {
                    employeeCycleService.updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.STARTED);
                }
            }
            profile.setProfileStatus(ProfileStatus.ACTIVE);
        } else if (updateLeavingDate.getLeavingDate() != null && updateLeavingDate.getLeavingDate().before(new Date())) {
            profile.setProfileStatus(ProfileStatus.INACTIVE);
            var empStartedCycles = employeeCycleDao.getByEmployeeIdAndStatus(employee.getUuid(), CycleStatus.STARTED);
            empStartedCycles.forEach(employeeCycle -> employeeCycleService.updateEmployeeCycleStatus(employeeCycle.getUuid(), CycleStatus.INACTIVE));
        }
        profileDao.update(profile);
    }


    @Override
    public List<Employee> getAll() {
        return employeeDao.getAll();
    }


    public void managerAccess(MultipartFile file) throws IOException {
        List<List<String>> rowValues = fileProcess(file);

        for (List<String> rowValue : rowValues) {
            if (rowValue.size() != 2) {
                continue;
            }
            String email = rowValue.get(0);
            String action = rowValue.get(1);

            if (StringUtils.isNotBlank(action) && StringUtils.isNotBlank(email)) {
                var employee = employeeDao.getByEmail(email);

                if (ADD.equalsIgnoreCase(action)) {
                    employee.setIsManager(Boolean.TRUE);
                } else if (REMOVE.equalsIgnoreCase(action)) {
                    var colleaguesByManager = getByManagerUuid(employee.getUuid());
                    if (!colleaguesByManager.isEmpty()) {
                        throw new InvalidInputException(EMPLOYEE_INTEGRATE_VIOLATION.code(), "Colleagues exists under this manager, Please update their manager details to proceed");
                    }
                    employee.setIsManager(Boolean.FALSE);
                }

                employeeDao.update(employee);
            }
        }
    }


    @Override
    public void updateManagerId(MultipartFile file) throws IOException {
        List<List<String>> rowDatas = fileProcess(file);
        for (List<String> rowData : rowDatas) {
            if (rowData.size() != 3) {
                return;
            }
            String employeeEmail = rowData.get(0);
            String managerEmail = rowData.get(1);
            String action = rowData.get(2);
            updateManager(employeeEmail, managerEmail, action);
        }
    }

    private void updateManager(String employeeEmail, String managerEmail, String action) {
        if (isValidInput(employeeEmail, managerEmail, action)) {
            Employee employee = getByEmail(employeeEmail);
            Employee manager = getByEmail(managerEmail);
            performAction(action, employee, manager);
        }
    }

    public Employee getByEmail(String email) {
        var employee = employeeDao.getByEmail(email.trim());
        if (employee == null) {
            throw new NotFoundException(EMPLOYEE_NOT_FOUND.code(), "Employee not found with email " + email);
        }
        return employee;
    }

    private boolean isValidInput(String employeeEmail, String managerEmail, String action) {
        return StringUtils.isNotBlank(employeeEmail)
                && StringUtils.isNotBlank(managerEmail)
                && StringUtils.isNotBlank(action);
    }

    private void performAction(String action, Employee employee, Employee manager) {
        switch (action.toLowerCase()) {
            case ADD:
                addManager(employee, manager);
                break;
            case REMOVE:
                removeManager(employee, manager);
                break;
            default:
                throw new InvalidInputException(INVALID_INPUT_EXCEPTION.code(), "Invalid action provided: " + action);
        }
    }

    private void addManager(Employee employee, Employee manager) {
        validateManagerAccess(manager);
        employee.setManagerUuid(manager.getUuid());
        employeeDao.update(employee);
    }

    private void removeManager(Employee employee, Employee manager) {
        if (manager.getUuid().equals(employee.getManagerUuid())) {
            employee.setManagerUuid(null);
            employeeDao.update(employee);
        } else {
            throw new InvalidInputException(INVALID_MANAGER_PROVIDED.code(), "Invalid Manager details provided");
        }
    }

    private void validateManagerAccess(Employee manager) {
        if (Boolean.FALSE.equals(manager.getIsManager())) {
            throw new InvalidInputException(MANAGER_ACCESS_NOT_FOUND.code(), "Manager access not granted for email: " + manager.getEmail());
        }
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
        var employeeUuid = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return employeeDao.getMe(employeeUuid);
    }

    @Override
    public SuccessResponseDto colleagueOnboard(MultipartFile file) throws IOException, MessagingException {
        var rowDatas = fileProcess(file);
        List<UUID> employeeUuids = new ArrayList<>();
        for (var rowData : rowDatas) {
            if (rowData.size() != 11) {
                continue;
            }

            var gender = rowData.get(3).equals("M") ? Gender.MALE : Gender.FEMALE;
            var mangerUuid = rowData.get(10).trim().isEmpty() ? null : UUID.fromString(rowData.get(10).trim());

            var decimalFormat = new DecimalFormat("0");
            decimalFormat.setMaximumFractionDigits(0);
            var employee = AddEmployeeDto.builder()
                    .firstName(rowData.get(0))
                    .lastName(rowData.get(1))
                    .email(rowData.get(2))
                    .gender(gender)
                    .dateOfBirth(parseDate(rowData.get(4)))
                    .phoneNumber(decimalFormat.format(Double.parseDouble(rowData.get(5))))
                    .joiningDate(parseDate(rowData.get(6)))
                    .leavingDate(parseDate(rowData.get(7)))
                    .departmentName(rowData.get(8).trim())
                    .isManager(rowData.get(9).trim())
                    .managerUuid(mangerUuid)
                    .build();
            employeeUuids.add(add(employee).getUuid());
        }
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(employeeUuids))
                .build();
    }

    private LocalDate parseDate(String value) {
        if (value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(PARSE_DATE));
    }

    public List<List<String>> fileProcess(MultipartFile file) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new InvalidInputException(SHEET_NOT_FOUND.code(), "Sheet Not Found");
            }

            List<List<String>> rowValues = new ArrayList<>();

            var headerRow = sheet.getRow(0);
            List<String> columnHeadings = new ArrayList<>();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                var cell = headerRow.getCell(i);
                if (cell == null) {
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(), "Invalid Column Headings");
                }
                columnHeadings.add(cell.toString());
            }

            var rows = sheet.rowIterator();
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                List<String> rowValue = new ArrayList<>();
                var row = (XSSFRow) rows.next();

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    var cell = row.getCell(i);
                    var value = cell == null ? "" : cell.toString();
                    rowValue.add(value);
                }
                rowValues.add(rowValue);
            }
            return rowValues;
        }
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

    public void sendSuccessfulEmployeeOnBoard(UUID employeeUuid) {
        Thread thread = new Thread(() -> {
            try {
                var employee = getById(employeeUuid);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);

                helper.setFrom(defaultEmail, emailTemplateSuccessfulOnboard);
                helper.setTo(employee.getEmail());

                helper.setSubject(emailTemplateSuccessfulOnboard);

                Context context = new Context();
                context.setVariable("name", employee.getFirstName() + " " + employee.getLastName());
                context.setVariable("email", employee.getEmail());
                context.setVariable("phoneNumber", employee.getPhoneNumber());
                context.setVariable("password", employee.getPassword());

                helper.setText(templateEngine.process(emailTemplateNameSuccessfulOnboard, context), true);
                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Error sending successful onboarding email for employee with UUID: {}", employeeUuid, e);
            }
        });
        thread.start();
    }
}
