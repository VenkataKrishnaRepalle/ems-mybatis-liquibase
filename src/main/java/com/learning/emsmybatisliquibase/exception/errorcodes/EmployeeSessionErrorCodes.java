package com.learning.emsmybatisliquibase.exception.errorcodes;

import com.learning.emsmybatisliquibase.exception.api.ErrorCodeAware;

public enum EmployeeSessionErrorCodes implements ErrorCodeAware {
    EMPLOYEE_SESSION_NOT_FOUND,
    EMPLOYEE_SESSION_NOT_CREATED,
    EMPLOYEE_SESSION_UPDATE_FAILED,
    EMPLOYEE_SESSION_DELETE_FAILED;

    @Override
    public String code() {
        return name();
    }
}
