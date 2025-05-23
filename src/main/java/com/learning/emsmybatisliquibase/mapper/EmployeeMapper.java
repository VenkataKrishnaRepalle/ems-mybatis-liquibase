package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee addEmployeeDtoToEmployee(AddEmployeeDto addEmployeeDto);

    AddEmployeeResponseDto employeeToAddEmployeeResponseDto(Employee employee);

    EmployeeAndManagerDto employeeToEmployeeAndManagerDto(Employee employee);

    EmployeeFullReportingChainDto employeeResponseDtoToEmployeeFullReportingChainDto(Employee employeeResponse);

    EmployeeDetailsDto employeeToEmployeeDetailsDto(Employee employee);
}
