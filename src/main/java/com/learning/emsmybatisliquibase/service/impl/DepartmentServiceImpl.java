package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static com.learning.emsmybatisliquibase.exception.errorcodes.DepartmentErrorCodes.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    private static final String COLLEAGUE_UUID = "colleague_uuid";

    private static final String COLLEAGUE_FULL_NAME = "colleague_full_name";

    private static final String EMAIL = "email";

    private static final String JOINING_DATE = "joining_date";

    private static final String LEAVING_DATE = "leaving_date";

    private static final String PROFILE_STATUS = "profile_status";

    private static final String MANAGER_FULL_NAME = "manager_full_name";

    private static final String DEPARTMENT_NAME = "department_name";

    private static final String DATE_PATTERN = "dd/MM/yyyy";


    @Override
    public Department add(AddDepartmentDto departmentDto) {
        var department = departmentDao.getByName(departmentDto.getName().toLowerCase());
        if (department != null) {
            return department;
        }
        var newDepartment = Department.builder()
                .uuid(UUID.randomUUID())
                .name(departmentDto.getName())
                .build();

        try {
            if (0 == departmentDao.insert(newDepartment)) {
                throw new IntegrityException(DEPARTMENT_NOT_CREATED.code(), "Department not created");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(DEPARTMENT_NOT_CREATED.code(), exception.getCause().getMessage());
        }
        return newDepartment;
    }

    @Override
    public Department update(UUID departmentUuid, AddDepartmentDto departmentDto) {
        var department = getById(departmentUuid);
        department.setName(departmentDto.getName());
        try {
            if (0 == departmentDao.update(department)) {
                throw new IntegrityException(DEPARTMENT_NOT_UPDATED.code(), "Department not updated");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(DEPARTMENT_NOT_UPDATED.code(), exception.getCause().getMessage());
        }
        return department;
    }

    @Override
    public void delete(UUID departmentUuid) {
        getById(departmentUuid);
        try {
            if (0 == departmentDao.delete(departmentUuid)) {
                throw new IntegrityException(DEPARTMENT_NOT_DELETED.code(), "Department not deleted");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException(DEPARTMENT_NOT_DELETED.code(), exception.getCause().getMessage());
        }
    }

    @Override
    public List<Department> getAll() {
        return departmentDao.getAll();
    }

    @Override
    public Department getById(UUID departmentUuid) {
        var department = departmentDao.get(departmentUuid);
        if (department == null) {
            throw new NotFoundException(DEPARTMENT_NOT_FOUND.code(),
                    "Department not exists with Id: " + departmentUuid);
        }
        return department;
    }

    @Override
    public void downloadDepartmentReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Department Report");
        Row headerRow = sheet.createRow(0);

        String[] headers = {COLLEAGUE_UUID, COLLEAGUE_FULL_NAME, EMAIL, JOINING_DATE, LEAVING_DATE,
                PROFILE_STATUS, MANAGER_FULL_NAME, DEPARTMENT_NAME};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        var departmentDetails = departmentDao.departmentReport();
        for (var department : departmentDetails) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(String.valueOf(department.getColleagueUuid()));
            row.createCell(1).setCellValue(department.getColleagueFullName());
            row.createCell(2).setCellValue(department.getEmail());
            row.createCell(3).setCellValue(formatDate(department.getJoiningDate()));
            row.createCell(4).setCellValue(formatDate(department.getLeavingDate()));
            row.createCell(5).setCellValue(department.getProfileStatus().toString());
            row.createCell(6).setCellValue(department.getManagerFullName());
            row.createCell(7).setCellValue(department.getDepartmentName());
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream("employee_data.xlsx")) {
            workbook.write(fileOutputStream);
        } finally {
            workbook.close();
        }
    }

    private String formatDate(Date date) {
        return date != null ? new SimpleDateFormat(DATE_PATTERN).format(date) : "";
    }

}