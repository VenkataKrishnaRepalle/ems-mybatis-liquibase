package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.SkillsDto;
import com.learning.emsmybatisliquibase.entity.Skills;

import java.util.List;
import java.util.UUID;

public interface SkillsService {
    Skills add(SkillsDto skillsDto);

    Skills update(UUID skillsUuid, SkillsDto skillsDto);

    Skills getById(UUID skillsUuid);

    List<Skills> getAll(UUID employeeUuid);

    void deleteById(UUID skillsUuid, UUID employeeUuid);
}
