package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.EmployeeRole;
import com.learning.emsmybatisliquibase.entity.RoleType;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class EmployeeRoleController {

    private final EmployeeRoleService employeeRoleService;

    @PostMapping("/add")
    public ResponseEntity<EmployeeRole> add(@RequestBody EmployeeRole employeeRole) {
        return new ResponseEntity<>(employeeRoleService.add(employeeRole), HttpStatus.CREATED);
    }

    @GetMapping("/getRoles/{employeeUuid}")
    public ResponseEntity<List<RoleType>> getRoles(@PathVariable UUID employeeUuid) {
        return new ResponseEntity<>(employeeRoleService.getRolesByEmployeeUuid(employeeUuid), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> delete(@RequestBody EmployeeRole employeeRole) {
        employeeRoleService.delete(employeeRole);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
