package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.EmployeeAndManagerDto;
import com.learning.emsmybatisliquibase.dto.EmployeeFullReportingChainDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeResponseDto;
import com.learning.emsmybatisliquibase.entity.Employee;
import org.mapstruct.Mapper;

@Mapper
public interface EmployeeMapper {

    Employee addEmployeeDtoToEmployee(AddEmployeeDto addEmployeeDto);

    AddEmployeeResponseDto employeeToAddEmployeeResponseDto(Employee employee);

    EmployeeAndManagerDto employeeToEmployeeAndManagerDto(Employee employee);

    EmployeeFullReportingChainDto employeeResponseDtoToEmployeeFullReportingChainDto(Employee employeeResponse);

}
