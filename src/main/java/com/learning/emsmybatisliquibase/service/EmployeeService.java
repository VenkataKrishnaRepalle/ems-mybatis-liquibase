package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.entity.Employee;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    AddEmployeeResponseDto add(AddEmployeeDto employeeDto) throws MessagingException, UnsupportedEncodingException;

    Employee getById(UUID id);

    void updateLeavingDate(UUID id, UpdateLeavingDateDto updateLeavingDate);

    List<Employee> viewAll();

    void managerAccess(MultipartFile file) throws IOException;

    void updateManagerId(MultipartFile file) throws IOException;

    List<Employee> getByManagerUuid(UUID managerId);

    void isManager(UUID uuid);

    List<EmployeeAndManagerDto> getFullTeam(UUID employeeId);

    List<List<String>> fileProcess(MultipartFile file) throws IOException;

    SuccessResponseDto colleagueOnboard(MultipartFile file) throws IOException, ParseException, MessagingException;

    EmployeeFullReportingChainDto getEmployeeFullReportingChain(UUID employeeId);
}
