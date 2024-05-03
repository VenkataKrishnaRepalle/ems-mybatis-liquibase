package com.learning.emsmybatisliquibase.controller;


import com.learning.emsmybatisliquibase.dto.AddEmployeeResponseDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeDto;
import com.learning.emsmybatisliquibase.dto.EmployeeAndManagerDto;
import com.learning.emsmybatisliquibase.dto.EmployeeFullReportingChainDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.dto.UpdateLeavingDateDto;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/me")
    public ResponseEntity<Employee> getMe() {
        return new ResponseEntity<>(employeeService.getMe(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<AddEmployeeResponseDto> addEmployee(@Valid @RequestBody AddEmployeeDto employeeDto) throws MessagingException, UnsupportedEncodingException {
        return new ResponseEntity<>(employeeService.add(employeeDto), HttpStatus.CREATED);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Employee> viewEmployee(@PathVariable UUID id) {
        return new ResponseEntity<>(employeeService.getById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/viewAll")
    public ResponseEntity<List<Employee>> viewAllEmployees() {
        return new ResponseEntity<>(employeeService.viewAll(), HttpStatus.OK);
    }

    @PostMapping("/updateLeavingDate/{id}")
    public ResponseEntity<HttpStatus> updateLeavingStatus(@PathVariable UUID id, @RequestBody UpdateLeavingDateDto updateLeavingDate) {
        employeeService.updateLeavingDate(id, updateLeavingDate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/managerAccess/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> managerAccess(@RequestParam(name = "file") MultipartFile file) throws IOException {
        employeeService.managerAccess(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/updateManagerId/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> updateManagerId(@RequestParam(name = "file") MultipartFile file) throws IOException {
        employeeService.updateManagerId(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/getByManagerId/{managerId}")
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable UUID managerId) {
        return new ResponseEntity<>(employeeService.getByManagerUuid(managerId), HttpStatus.OK);
    }

    @GetMapping(value = "/getFullTeam/{employeeId}")
    public ResponseEntity<List<EmployeeAndManagerDto>> getFullTeam(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(employeeService.getFullTeam(employeeId), HttpStatus.OK);
    }

    @GetMapping(value = "getEmployeeFullReportingChain/{employeeId}")
    public ResponseEntity<EmployeeFullReportingChainDto> getEmployeeFullReportingChain(@PathVariable UUID employeeId) {
        return new ResponseEntity<>(employeeService.getEmployeeFullReportingChain(employeeId), HttpStatus.OK);
    }

    @PostMapping(value = "employee-onboard/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponseDto> colleagueOnboard(@RequestParam(name = "file") MultipartFile file) throws IOException, ParseException, MessagingException {
        return new ResponseEntity<>(employeeService.colleagueOnboard(file), HttpStatus.OK);
    }
}
