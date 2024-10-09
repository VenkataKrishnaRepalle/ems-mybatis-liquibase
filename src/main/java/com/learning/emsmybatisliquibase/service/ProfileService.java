package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.Profile;

import java.util.UUID;

public interface ProfileService {

    Profile getByEmployeeUuid(UUID employeeUuid);

    Profile insert(Profile profile);

    Profile update(Profile profile);
}
