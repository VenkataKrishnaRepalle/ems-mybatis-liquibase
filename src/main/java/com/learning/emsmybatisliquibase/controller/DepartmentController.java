package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.AddDepartmentDto;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.service.DepartmentService;
import lombok.AllArgsConstructor;
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
import org.springframework.web.bind.annotation.PutMapping;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/addDepartment")
    public ResponseEntity<Department> add(@RequestBody AddDepartmentDto department) {
        return new ResponseEntity<>(departmentService.add(department), HttpStatus.CREATED);
    }

    @PutMapping("/updateDepartment/{departmentUuid}")
    public ResponseEntity<Department> update(@PathVariable UUID departmentUuid,
                                             @RequestBody AddDepartmentDto department) {
        return new ResponseEntity<>(departmentService.update(departmentUuid, department),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/deleteDepartment/{departmentUuid}")
    public ResponseEntity<HttpStatus> delete(@PathVariable UUID departmentUuid) {
        departmentService.delete(departmentUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Department>> getAll() {
        return new ResponseEntity<>(departmentService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/download-department-report")
    public ResponseEntity<HttpStatus> downloadDepartmentReport() throws IOException {
        departmentService.downloadDepartmentReport();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

