package com.learning.emsmybatisliquibase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto {

    private String email;

    private String otp;

    private String password;

    private String confirmPassword;
}
