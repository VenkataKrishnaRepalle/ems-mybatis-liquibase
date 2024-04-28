package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.dto.EmployeeDepartmentAndManagerDetailsDto;
import com.learning.emsmybatisliquibase.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface DepartmentDao {
    Department get(@Param("uuid") UUID uuid);

    Department getByName(@Param("name") String name);

    int insert(@Param("department") Department department);

    int update(@Param("department") Department department);

    int delete(@Param("departmentUuid") UUID departmentUuid);

    List<Department> getAll();

    List<EmployeeDepartmentAndManagerDetailsDto> departmentReport();
}
