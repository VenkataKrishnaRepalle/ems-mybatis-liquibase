package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.ForgotPasswordDto;
import com.learning.emsmybatisliquibase.dto.PasswordDto;
import com.learning.emsmybatisliquibase.dto.ResetPasswordDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.Password;

import java.util.UUID;

public interface PasswordService {

    void create(UUID employeeUuid, PasswordDto passwordDto);

    Password update(Password password);

    SuccessResponseDto forgotPassword(UUID uuid, ForgotPasswordDto forgotPasswordDto);

    SuccessResponseDto resetPassword(UUID uuid, ResetPasswordDto resetPasswordDto);
}
