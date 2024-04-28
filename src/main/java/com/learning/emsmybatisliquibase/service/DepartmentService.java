package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department add(AddDepartmentDto department);

    void departmentPermission(MultipartFile file) throws IOException;

    Department update(UUID departmentUuid, AddDepartmentDto department);

    void delete(UUID departmentUuid);

    List<Department> getAll();

    void downloadDepartmentReport() throws IOException;
}
