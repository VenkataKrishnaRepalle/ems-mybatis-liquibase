package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import com.learning.emsmybatisliquibase.entity.EmployeeRole;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
                throw new InvalidInputException("Employee already has this role");
            }
        });

        if (0 == employeeRoleDao.insert(employeeRole)) {
            throw new InvalidInputException("Insert failed");
        }

        return employeeRole;
    }

    @Override
    public void delete(EmployeeRole employeeRole) {
        employeeService.getById(employeeRole.getEmployeeUuid());
        if (0 == employeeRoleDao.delete(employeeRole.getEmployeeUuid(), employeeRole.getRole().toString())) {
            throw new InvalidInputException("Delete failed");
        }
    }

    @Override
    public List<EmployeeRole> getRolesByEmployeeUuid(UUID employeeUuid) {
        employeeService.getById(employeeUuid);
        return employeeRoleDao.getByEmployeeUuid(employeeUuid);
    }
}
