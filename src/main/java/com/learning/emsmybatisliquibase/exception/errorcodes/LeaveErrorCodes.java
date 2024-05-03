package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum LeaveErrorCodes implements ErrorCodeAware {
    LEAVE_NOT_FOUND,
    LEAVE_NOT_CREATED,
    LEAVE_NOT_UPDATED,
    LEAVE_ALREADY_EXIST;

    @Override
    public String code() {
        return name();
    }
}
