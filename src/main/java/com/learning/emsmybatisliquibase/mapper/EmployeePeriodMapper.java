package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.FullEmployeePeriodDto;
import com.learning.emsmybatisliquibase.entity.EmployeePeriod;
import org.mapstruct.Mapper;

@Mapper
public interface EmployeePeriodMapper {
    FullEmployeePeriodDto employeePeriodToFullEmployeePeriodDto(EmployeePeriod employeePeriod);
}
