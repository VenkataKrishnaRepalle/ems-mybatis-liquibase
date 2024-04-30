package com.learning.emsmybatisliquibase.service;


import com.learning.emsmybatisliquibase.entity.Education;

import java.util.List;
import java.util.UUID;

public interface EducationService {
    Education save(Education educationDto);

    Education update(Education educationDto, UUID uuid);

    Education getById(UUID uuid);

    List<Education> getAll(UUID employeeUuid);

    void deleteById(UUID uuid);
}
