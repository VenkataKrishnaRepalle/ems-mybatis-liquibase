package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.FullEmployeeCycleDto;
import com.learning.emsmybatisliquibase.entity.EmployeePeriod;
import org.mapstruct.Mapper;

@Mapper
public interface EmployeeCycleMapper {
    FullEmployeeCycleDto employeeCycleToFullEMployeeCycleDto(EmployeePeriod employeePeriod);
}
