package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum OtpErrorCodes implements ErrorCodeAware {
    OTP_SENT_FAILED,
    OTP_UPDATE_FAILED,
    OTP_REGENERATE,
    OTP_SUCCESSFUL_VERIFICATION,
    OTP_VERIFICATION_FAILED,
    OTP_INVALID_SIZED,
    INVALID_OTP,
    OTP_DELETE_FAILED,
    OTP_NOT_FOUND,
    OTP_NOT_VALID;

    @Override
    public String code() {
        return name();
    }
}
