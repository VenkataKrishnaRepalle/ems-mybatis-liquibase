package com.learning.emsmybatisliquibase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {

    private String email;

    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;
}
