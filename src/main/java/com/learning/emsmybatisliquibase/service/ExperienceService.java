package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.Experience;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {

    Experience getById(UUID experienceId);

    List<Experience> getAllByEmployeeUuid(UUID employeeId);

    List<Experience> add(UUID employeeUuid, List<Experience> experiences);

    List<Experience> update(UUID employeeUuid, List<Experience> experiences);

    void deleteById(UUID experienceId);
}
