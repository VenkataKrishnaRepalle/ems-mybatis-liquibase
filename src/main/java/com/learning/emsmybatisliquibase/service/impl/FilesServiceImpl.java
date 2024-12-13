package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeDto;
import com.learning.emsmybatisliquibase.dto.FileType;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.Gender;
import com.learning.emsmybatisliquibase.entity.Profile;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.DepartmentService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.FilesService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.DepartmentErrorCodes.PROFILE_NOT_UPDATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.*;
import static com.learning.emsmybatisliquibase.exception.errorcodes.FileErrorCodes.INVALID_COLUMN_HEADINGS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.FileErrorCodes.SHEET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FilesServiceImpl implements FilesService {

    private final EmployeeService employeeService;

    private final DepartmentDao departmentDao;

    private final DepartmentService departmentService;

    private final ProfileDao profileDao;

    private static final String ACTION = "action";

    private static final String ADD = "add";

    private static final String REMOVE = "remove";

    private static final String PARSE_DATE = "dd/MM/yyyy";

    @Override
    public SuccessResponseDto colleagueOnboard(MultipartFile file) throws IOException, MessagingException {
        var rowDatas = fileProcess(file, FileType.COLLEAGUE_ONBOARD);
        List<UUID> employeeUuids = new ArrayList<>();
        for (var rowData : rowDatas) {
            if (rowData.size() != 14) {
                continue;
            }

            var decimalFormat = new DecimalFormat("0");
            decimalFormat.setMaximumFractionDigits(0);
            var employee = AddEmployeeDto.builder()
                    .firstName(rowData.get(0))
                    .lastName(rowData.get(1))
                    .email(rowData.get(2))
                    .gender(rowData.get(3).equals("M") ? Gender.MALE : Gender.FEMALE)
                    .dateOfBirth(parseDate(rowData.get(4)))
                    .phoneNumber(decimalFormat.format(Double.parseDouble(rowData.get(5))))
                    .joiningDate(parseDate(rowData.get(6)))
                    .leavingDate(parseDate(rowData.get(7)))
                    .departmentName(rowData.get(8).trim())
                    .isManager(rowData.get(9).trim())
                    .managerUuid(rowData.get(10).trim().isEmpty() ? null : UUID.fromString(rowData.get(10).trim()))
                    .jobTitle(rowData.get(11))
                    .password(rowData.get(12))
                    .confirmPassword(rowData.get(13))
                    .build();
            employeeUuids.add(employeeService.add(employee).getUuid());
        }
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(employeeUuids))
                .build();
    }

    public void managerAccess(MultipartFile file) throws IOException {
        List<List<String>> rowValues = fileProcess(file, FileType.MANAGER_ACCESS);

        for (List<String> rowValue : rowValues) {
            if (rowValue.size() != 2) {
                continue;
            }
            String email = rowValue.get(0);
            String action = rowValue.get(1);

            if (StringUtils.isNotBlank(action) && StringUtils.isNotBlank(email)) {
                var employee = employeeService.getByEmail(email);

                if (ADD.equalsIgnoreCase(action)) {
                    employee.setIsManager(Boolean.TRUE);
                } else if (REMOVE.equalsIgnoreCase(action)) {
                    var colleaguesByManager = employeeService.getByManagerUuid(employee.getUuid());
                    if (!colleaguesByManager.isEmpty()) {
                        throw new InvalidInputException(EMPLOYEE_INTEGRATE_VIOLATION.code(),
                                "Colleagues exists under this manager, Please update their manager details to proceed");
                    }
                    employee.setIsManager(Boolean.FALSE);
                }
                employeeService.update(employee);
            }
        }
    }

    @Override
    public void updateManagerId(MultipartFile file) throws IOException {
        List<List<String>> rowDatas = fileProcess(file, FileType.UPDATE_MANAGER);
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
            Employee employee = employeeService.getByEmail(employeeEmail);
            Employee manager = employeeService.getByEmail(managerEmail);
            performAction(action, employee, manager);
        }
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
        employeeService.update(employee);
    }

    private void validateManagerAccess(Employee manager) {
        if (Boolean.FALSE.equals(manager.getIsManager())) {
            throw new InvalidInputException(MANAGER_ACCESS_NOT_FOUND.code(),
                    "Manager access not granted for email: " + manager.getEmail());
        }
    }

    private void removeManager(Employee employee, Employee manager) {
        if (manager.getUuid().equals(employee.getManagerUuid())) {
            employee.setManagerUuid(null);
            employeeService.update(employee);
        } else {
            throw new InvalidInputException(INVALID_MANAGER_PROVIDED.code(), "Invalid Manager details provided");
        }
    }

    public void departmentPermission(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new NotFoundException(SHEET_NOT_FOUND.code(), "Sheet Not Found");
        }
        var rowValues = fileProcess(file, FileType.DEPARTMENT_PERMISSION);

        for (List<String> value : rowValues) {
            var department = departmentDao.getByName(value.getFirst().trim());

            var employee = employeeService.getByEmail(value.get(1));
            if (department == null) {
                department = departmentService.add(new AddDepartmentDto(value.getFirst().trim()));
            }

            var action = value.get(2);
            if (action.equalsIgnoreCase(ADD)) {
                var profile = profileDao.get(employee.getUuid());
                profile.setDepartmentUuid(department.getUuid());
                updateProfile(profile);
            } else if (action.equalsIgnoreCase(REMOVE)) {
                var profile = profileDao.get(employee.getUuid());
                if (profile.getDepartmentUuid() != null && department.getUuid().equals(profile.getDepartmentUuid())) {
                    profile.setDepartmentUuid(null);
                    updateProfile(profile);
                }
            }
            workbook.close();
        }
    }

    private void updateProfile(Profile profile) {
        try {
            if (0 == profileDao.update(profile)) {
                throw new IntegrityException(PROFILE_NOT_UPDATED.code(), "Profile not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(PROFILE_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
    }

    private LocalDate parseDate(String value) {
        if (value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(PARSE_DATE));
    }

    public List<List<String>> fileProcess(MultipartFile file, FileType fileType) throws IOException {
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
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(),
                            INVALID_COLUMN_HEADINGS.code().toLowerCase());
                }
                columnHeadings.add(cell.toString().toLowerCase());
            }

            validateColumnHeadings(columnHeadings, fileType);

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

    private void validateColumnHeadings(List<String> columnHeadings, FileType fileType) {
        switch (fileType) {
            case COLLEAGUE_ONBOARD:
                if (!columnHeadings.get(0).equals("first_name") &&
                        !columnHeadings.get(1).equals("last_name") &&
                        !columnHeadings.get(2).equals("email") &&
                        !columnHeadings.get(3).equals("gender") &&
                        !columnHeadings.get(4).equals("date_of_birth") &&
                        !columnHeadings.get(5).equals("phone_number") &&
                        !columnHeadings.get(6).equals("joining_date") &&
                        !columnHeadings.get(7).equals("leaving_date") &&
                        !columnHeadings.get(8).equals("department_name") &&
                        !columnHeadings.get(9).equals("is_manager") &&
                        !columnHeadings.get(10).equals("manager_uuid") &&
                        !columnHeadings.get(11).equals("job_title") &&
                        !columnHeadings.get(12).equals("password") &&
                        !columnHeadings.get(13).equals("confirm_password")) {
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(),
                            "Please correct column headings");
                }
                break;
            case MANAGER_ACCESS:
                if (!columnHeadings.contains("manager_email") || !columnHeadings.contains(ACTION)) {
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(),
                            INVALID_COLUMN_HEADINGS.code().toLowerCase());
                }
                break;
            case UPDATE_MANAGER:
                if (!columnHeadings.contains("colleague_email") || !columnHeadings.contains("manager_email")
                        || !columnHeadings.contains(ACTION)) {
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(),
                            INVALID_COLUMN_HEADINGS.code().toLowerCase());
                }
                break;
            case DEPARTMENT_PERMISSION:
                if (!columnHeadings.contains("department_name") && !columnHeadings.contains("email") &&
                        !columnHeadings.contains(ACTION)) {
                    throw new InvalidInputException(INVALID_COLUMN_HEADINGS.code(),
                            INVALID_COLUMN_HEADINGS.code().toLowerCase());
                }
                break;
            default:
                break;
        }
    }
}
