package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department add(AddDepartmentDto department);

    Department update(UUID departmentUuid, AddDepartmentDto department);

    void delete(UUID departmentUuid);

    List<Department> getAll();

    Department getById(UUID departmentUuid);

    void downloadDepartmentReport() throws IOException;
}
