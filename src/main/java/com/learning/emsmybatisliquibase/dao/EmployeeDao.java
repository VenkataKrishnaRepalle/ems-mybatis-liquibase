package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EmployeeDao {

    Employee get(@Param("uuid") UUID uuid);

    Employee getByEmail(@Param("email") String email);

    int insert(@Param("employee") Employee employee);

    int updateLeavingDate(@Param("leavingDate") Date leavingDate, @Param("employeeUuid") UUID employeeUuid);

    List<Employee> getAll();

    Long count();

    List<Employee> getAllByManagerUuid(@Param("managerUuid") UUID managerUuid);

    int update(@Param("employee") Employee employee);
}
