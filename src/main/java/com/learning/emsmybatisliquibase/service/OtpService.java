package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.pagination.RequestQuery;
import com.learning.emsmybatisliquibase.entity.OtpAuth;
import com.learning.emsmybatisliquibase.entity.enums.OtpAuthType;

import java.util.UUID;

public interface OtpService {

    OtpAuth getByUuid(UUID uuid);

    OtpAuth get(RequestQuery requestQuery);

    OtpAuth sendOtp(String email, OtpAuthType type);

    OtpAuth verifyOtp(UUID employeeUuid, String otp, OtpAuthType type);

    void update(OtpAuth otpAuth);

    void delete(UUID uuid);
}
