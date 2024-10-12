package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.PasswordDto;
import com.learning.emsmybatisliquibase.entity.Password;

import java.util.UUID;

public interface PasswordService {

    Password create(UUID employeeUuid, PasswordDto passwordDto);

    Password update(Password password);
}
