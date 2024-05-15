package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import com.learning.emsmybatisliquibase.entity.EmployeeRole;
import com.learning.emsmybatisliquibase.entity.RoleType;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeRoleErrorCodes.EMPLOYEE_ROLE_ALREADY_EXISTS;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeRoleErrorCodes.EMPLOYEE_ROLE_NOT_CREATED;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeRoleErrorCodes.EMPLOYEE_ROLE_NOT_DELETED;

@Service
@RequiredArgsConstructor
public class EmployeeRoleServiceImpl implements EmployeeRoleService {

    private final EmployeeRoleDao employeeRoleDao;

    private final EmployeeService employeeService;

    @Override
    public EmployeeRole add(EmployeeRole employeeRole) {
        employeeService.getById(employeeRole.getEmployeeUuid());

        var employeeRoleList = employeeRoleDao.getByEmployeeUuid(employeeRole.getEmployeeUuid());

        employeeRoleList.forEach(employeeRole1 -> {
            if (employeeRole1.getRole().equals(employeeRole.getRole())) {
                throw new InvalidInputException(EMPLOYEE_ROLE_ALREADY_EXISTS.code(), "Employee already has this role");
            }
        });

        if (0 == employeeRoleDao.insert(employeeRole)) {
            throw new InvalidInputException(EMPLOYEE_ROLE_NOT_CREATED.code(), "Insert failed");
        }

        return employeeRole;
    }

    @Override
    public void delete(EmployeeRole employeeRole) {
        employeeService.getById(employeeRole.getEmployeeUuid());
        if (0 == employeeRoleDao.delete(employeeRole.getEmployeeUuid(), employeeRole.getRole().toString())) {
            throw new InvalidInputException(EMPLOYEE_ROLE_NOT_DELETED.code(), "Delete failed");
        }
    }

    @Override
    public List<RoleType> getRolesByEmployeeUuid(UUID employeeUuid) {
        var employee = employeeService.getById(employeeUuid);
        List<RoleType> roles = new ArrayList<>();
        if (employee != null) {
            roles.add(RoleType.EMPLOYEE);
            if (Boolean.TRUE.equals(employee.getIsManager())) roles.add(RoleType.MANAGER);
        }

        var employeeExtraRoles = employeeRoleDao.getByEmployeeUuid(employeeUuid);
        employeeExtraRoles.forEach(employeeRole -> roles.add(employeeRole.getRole()));
        return roles;
    }
}
