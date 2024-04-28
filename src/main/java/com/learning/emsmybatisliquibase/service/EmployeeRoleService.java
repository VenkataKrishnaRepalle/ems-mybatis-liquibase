package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.EmployeeRole;

import java.util.List;
import java.util.UUID;

public interface EmployeeRoleService {

    EmployeeRole add(EmployeeRole employeeRole);

    void delete(EmployeeRole employeeRole);

    List<EmployeeRole> getRolesByEmployeeUuid(UUID employeeUuid);
}
