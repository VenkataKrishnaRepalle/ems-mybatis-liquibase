package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.entity.Password;

import java.util.UUID;

public interface PasswordService {

    Password create(UUID employeeUuid, String password);

    Password update(Password password);
}
