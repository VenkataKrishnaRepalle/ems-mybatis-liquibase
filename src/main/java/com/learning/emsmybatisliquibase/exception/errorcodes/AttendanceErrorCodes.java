package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum AttendanceErrorCodes implements ErrorCodeAware {
    ATTENDANCE_NOT_EXISTS,
    ATTENDANCE_ALREADY_EXISTS,
    ATTENDANCE_NOT_CREATED,
    ATTENDANCE_NOT_UPDATED;
    @Override
    public String code() {
        return name();
    }
}
