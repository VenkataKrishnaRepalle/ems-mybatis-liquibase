package com.learning.emsmybatisliquibase.dto;

import com.learning.emsmybatisliquibase.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeePaginationResponseDto {

    private List<Employee> employees;

    private Long count;
}
