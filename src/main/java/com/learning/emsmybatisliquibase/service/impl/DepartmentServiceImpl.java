package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.exception.AlreadyFoundException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.service.DepartmentService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    private final EmployeeDao employeeDao;

    private final EmployeeService employeeService;

    private static final String ADD = "add";

    private static final String REMOVE = "remove";

    private static final String COLLEAGUE_UUID = "colleague_uuid";
    private static final String COLLEAGUE_FULL_NAME = "colleague_full_name";
    private static final String EMAIL = "email";
    private static final String JOINING_DATE = "joining_date";
    private static final String LEAVING_DATE = "leaving_date";
    private static final String PROFILE_STATUS = "profile_status";
    private static final String MANAGER_FULL_NAME = "manager_full_name";
    private static final String DEPARTMENT_NAME = "department_name";
    private final ProfileDao profileDao;


    @Override
    public Department add(AddDepartmentDto departmentDto) {
        var isDepartmentExists = departmentDao.getByName(departmentDto.getName());
        if (isDepartmentExists != null) {
            throw new AlreadyFoundException("Department already exists with name " + departmentDto.getName());
        }
        var department = Department.builder()
                .uuid(UUID.randomUUID())
                .name(departmentDto.getName())
                .build();
        departmentDao.insert(department);
        return department;
    }

    public void departmentPermission(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new NotFoundException("Sheet Not Found");
        }
        var rowValues = employeeService.fileProcess(file);

        for (List<String> value : rowValues) {
            var department = departmentDao.getByName(value.get(0).trim());

            var employee = employeeDao.getByEmail(value.get(1));
            if (department == null && employee != null) {
                department = add(new AddDepartmentDto(value.get(0).trim()));
            }

            var action = value.get(2);
            if (employee != null) {
                if (action.equalsIgnoreCase(ADD)) {
                    var profile = profileDao.get(employee.getUuid());
                    profile.setDepartmentUuid(department.getUuid());
                    profileDao.update(profile);
                } else if (action.equalsIgnoreCase(REMOVE)) {
                    var profile = profileDao.get(employee.getUuid());
                    if (profile.getDepartmentUuid() != null && department.getUuid().equals(profile.getDepartmentUuid())) {
                        profile.setDepartmentUuid(null);
                        profileDao.update(profile);
                    }
                }
            }
        }
    }

    @Override
    public Department update(UUID departmentUuid, AddDepartmentDto departmentDto) {
        var department = isDepartmentExists(departmentUuid);
        department.setName(departmentDto.getName());
        departmentDao.update(department);
        return department;
    }

    @Override
    public void delete(UUID departmentUuid) {
        isDepartmentExists(departmentUuid);
        departmentDao.delete(departmentUuid);
    }

    @Override
    public List<Department> getAll() {
        return departmentDao.getAll();
    }

    @Override
    public void downloadDepartmentReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Department Report");
        Row headerRow = sheet.createRow(0);

        String[] headers = {COLLEAGUE_UUID, COLLEAGUE_FULL_NAME, EMAIL, JOINING_DATE, LEAVING_DATE, PROFILE_STATUS, MANAGER_FULL_NAME, DEPARTMENT_NAME};

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
            row.createCell(3).setCellValue(department.getJoiningDate() != null ? department.getJoiningDate().toString() : "");
            row.createCell(4).setCellValue(department.getLeavingDate() != null ? department.getLeavingDate().toString() : "");
            row.createCell(5).setCellValue(department.getProfileStatus().toString());
            row.createCell(6).setCellValue(department.getManagerFullName());
            row.createCell(7).setCellValue(department.getDepartmentName());
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream("employee_data.xlsx")) {
            workbook.write(fileOutputStream);
        }
        workbook.close();
    }

    private Department isDepartmentExists(UUID departmentUuid) {
        var department = departmentDao.get(departmentUuid);
        if (department == null) {
            throw new NotFoundException("Department not exists with Id: " + departmentUuid);
        }
        return department;
    }

}