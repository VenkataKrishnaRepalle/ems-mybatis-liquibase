package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeDto;
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

    private static final String ADD = "add";

    private static final String REMOVE = "remove";

    private static final String PARSE_DATE = "dd/MM/yyyy";


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
            employeeUuids.add(employeeService.add(employee).getUuid());
        }
        return SuccessResponseDto.builder()
                .success(Boolean.TRUE)
                .data(String.valueOf(employeeUuids))
                .build();
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
                var employee = employeeService.getByEmail(email);

                if (ADD.equalsIgnoreCase(action)) {
                    employee.setIsManager(Boolean.TRUE);
                } else if (REMOVE.equalsIgnoreCase(action)) {
                    var colleaguesByManager = employeeService.getByManagerUuid(employee.getUuid());
                    if (!colleaguesByManager.isEmpty()) {
                        throw new InvalidInputException(EMPLOYEE_INTEGRATE_VIOLATION.code(), "Colleagues exists under this manager, Please update their manager details to proceed");
                    }
                    employee.setIsManager(Boolean.FALSE);
                }
                employeeService.update(employee);
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
            throw new InvalidInputException(MANAGER_ACCESS_NOT_FOUND.code(), "Manager access not granted for email: " + manager.getEmail());
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
        var rowValues = fileProcess(file);

        for (List<String> value : rowValues) {
            var department = departmentDao.getByName(value.get(0).trim());

            var employee = employeeService.getByEmail(value.get(1));
            if (department == null) {
                department = departmentService.add(new AddDepartmentDto(value.get(0).trim()));
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
}
